use std::collections::HashMap;

use rand::{distributions::Alphanumeric, thread_rng, Rng};
use reqwest::Client;
use serde::{de::DeserializeOwned, Deserialize, Serialize};
use uuid::Uuid;

use crate::{
    config::GammaConfig,
    error::{GammaError, GammaResult},
};

/// The `state` variable sent to gamma using auth, required for verifying the session after
/// gamma redirects back to us.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GammaState(String);

/// The reslt of gamma_init_auth.
pub struct GammaInit {
    /// The state that should be stored and later used to verify the redirect.
    pub state: GammaState,
    /// The URL to redirect to.
    pub redirect_to: String,
}

/// Begins the auth flow with gamma, returning a GammaInit containing a redirect url to which the
/// user should be redirected to.
pub fn gamma_init_auth(config: &GammaConfig) -> GammaResult<GammaInit> {
    let state = GammaState::new();

    let scopes = urlencoding::encode(&config.scopes);

    let redirect_uri = format!(
        "{}/oauth2/authorize?response_type=code&client_id={}&scope={scopes}&redirect_uri={}&state={}",
        config.gamma_url, config.gamma_client_id, config.gamma_redirect_uri, state.get_state()
    );

    Ok(GammaInit {
        state,
        redirect_to: redirect_uri,
    })
}

impl GammaState {
    /// Generate a new state that can be used when querying gamma.
    pub fn new() -> Self {
        Self(
            thread_rng()
                .sample_iter(&Alphanumeric)
                .take(32)
                .map(char::from)
                .collect(),
        )
    }

    /// Create a new gamma state from the provided string.
    pub fn get_state_str(state: String) -> Self {
        Self(state)
    }

    /// Returns the contained state
    pub fn get_state(&self) -> &str {
        &self.0
    }

    /// When receiving a callback from the gamma API.
    pub async fn gamma_callback<QueryParams, QueryName, QueryValue>(
        &self,
        config: &GammaConfig,
        query_params: QueryParams,
    ) -> GammaResult<GammaAccessToken>
    where
        QueryParams: IntoIterator<Item = (QueryName, QueryValue)>,
        QueryName: AsRef<str>,
        QueryValue: AsRef<str>,
    {
        let params: HashMap<String, String> = query_params
            .into_iter()
            .map(|(a, b)| (a.as_ref().to_string(), b.as_ref().to_string()))
            .collect();

        if params.get("state") != Some(&self.0) {
            return Err(GammaError::GammaStateMissmatch);
        }

        let Some(code) = params.get("code") else {
            return Err(GammaError::NoCodeReceived);
        };

        gamma_get_oauth2_token(config, code.into()).await
    }

    /// Same as `gamma_callback` but providing the required parameters directly.
    /// Note that `callback_state` and `callback_code` should be the values received from gamma in
    /// the callback redirect as query parameters.
    pub async fn gamma_callback_params<State>(
        &self,
        config: &GammaConfig,
        callback_state: State,
        callback_code: String,
    ) -> GammaResult<GammaAccessToken>
    where
        State: AsRef<str>,
    {
        if callback_state.as_ref() != self.get_state() {
            return Err(GammaError::GammaStateMissmatch);
        }

        gamma_get_oauth2_token(config, callback_code).await
    }
}

impl Default for GammaState {
    fn default() -> Self {
        Self::new()
    }
}

impl From<String> for GammaState {
    fn from(value: String) -> Self {
        Self(value)
    }
}

#[derive(Debug, Clone, Serialize)]
struct GammaTokenRequest {
    client_id: String,
    client_secret: String,
    code: String,
    redirect_uri: String,
    grant_type: String,
}

#[derive(Debug, Clone, Deserialize)]
struct GammaTokenResponse {
    access_token: String,
}

/// An oauth2 access token that can be used to call gamma APIs on behalf of a user.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GammaAccessToken(String);

/// Retrieve a gamma oauth2 token from the code received in a callback.
pub async fn gamma_get_oauth2_token(
    config: &GammaConfig,
    code: String,
) -> GammaResult<GammaAccessToken> {
    let client = Client::new();
    let url = format!("{}/oauth2/token", config.gamma_url);

    let request = client
        .post(&url)
        .form(&GammaTokenRequest {
            client_id: config.gamma_client_id.clone(),
            client_secret: config.gamma_client_secret.clone(),
            code,
            redirect_uri: config.gamma_redirect_uri.clone(),
            grant_type: "authorization_code".into(),
        })
        .header("accept", "application/json")
        .send()
        .await;

    let body: GammaTokenResponse =
        handle_gamma_request(request, "Get token endpoint".into()).await?;

    Ok(GammaAccessToken(body.access_token))
}

/// A gamma user retrieved from the OpenID Connect API.
#[derive(Clone, Debug, Deserialize, Serialize)]
pub struct GammaOpenIDUser {
    /// The ID of the gamma user.
    #[serde(rename = "sub")]
    pub user_id: Uuid,
    /// The chalmers ID of this person.
    pub cid: String,
    /// The IT nick of this person.
    #[serde(rename = "nickname")]
    pub nick: String,
    /// The firstname of the person.
    pub given_name: String,
    /// The family name of the person.
    pub family_name: String,
    /// A url pointing to the picture uploaded by this user (if any).
    #[serde(rename = "picture")]
    pub avatar_url: String,

    /// The email the user has registered in gamma.
    /// Requires the `email` scope!
    pub email: Option<String>,
}

impl GammaAccessToken {
    /// Get the openid user information for this gamma user.
    pub async fn get_current_user(&self, config: &GammaConfig) -> GammaResult<GammaOpenIDUser> {
        let client = Client::new();
        let url = format!("{}/oauth2/userinfo", config.gamma_url);

        let request = client.get(&url).bearer_auth(self.0.clone()).send().await;

        let body: GammaOpenIDUser =
            handle_gamma_request(request, "Get userinfo endpoint".into()).await?;
        Ok(body)
    }
}

async fn handle_gamma_request<T>(
    request: reqwest::Result<reqwest::Response>,
    context: String,
) -> GammaResult<T>
where
    T: DeserializeOwned,
{
    let response = request.map_err(|err| GammaError::FailedSendingRequest {
        context: context.clone(),
        err,
    })?;

    let status = response.status();
    if !status.is_success() {
        let body_str = response
            .text()
            .await
            .unwrap_or("Failed to read response body".into());

        return Err(GammaError::ErrorResponse {
            context: context.clone(),
            status,
            body_str,
        });
    }

    let body: T =
        response
            .json()
            .await
            .map_err(|error| GammaError::FailedToDeserializeResponse {
                context: context.clone(),
                error,
            })?;

    Ok(body)
}
