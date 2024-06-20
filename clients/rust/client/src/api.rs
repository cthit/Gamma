use reqwest::{header::AUTHORIZATION, Client, RequestBuilder};
use serde::{de::DeserializeOwned, Deserialize, Serialize};
use uuid::Uuid;

use crate::{
    config::GammaConfig,
    error::{GammaError, GammaResult},
};

const PRE_SHARED: &str = "pre-shared";

/// A client to be used in order to perform calls to the gamma API requiring an API key.
pub struct GammaClient {
    client: Client,
    gamma_url: String,
    gamma_api_key: String,
}

impl GammaClient {
    /// Create a new GammaClient from the provided config.
    pub fn new(config: &GammaConfig) -> Self {
        Self {
            client: Client::new(),
            gamma_url: format!("{}/api/client", config.gamma_url),
            gamma_api_key: config.gamma_api_key.clone(),
        }
    }

    /// Get all groups.
    pub async fn get_groups(&self) -> GammaResult<Vec<GammaGroup>> {
        let request = self.client.get(format!("{}/v1/groups", self.gamma_url));

        let groups: Vec<GammaGroup> = self
            .handle_gamma_request(request, "get groups endpoint")
            .await?;

        Ok(groups)
    }

    /// Get all super groups.
    pub async fn get_super_groups(&self) -> GammaResult<Vec<GammaSuperGroup>> {
        let request = self
            .client
            .get(format!("{}/v1/superGroups", self.gamma_url));

        let super_groups: Vec<GammaSuperGroup> = self
            .handle_gamma_request(request, "get supergroups endpoint")
            .await?;

        Ok(super_groups)
    }

    /// Get all users that have accepted this client (this is usually done by authorizing against this client at least once).
    pub async fn get_users(&self) -> GammaResult<Vec<GammaUser>> {
        let request = self.client.get(format!("{}/v1/users", self.gamma_url));

        let users = self
            .handle_gamma_request(request, "get users endpoint")
            .await?;

        Ok(users)
    }

    /// Get all groups that the user with the provided `user_id` are a part of.
    pub async fn get_groups_for_user(&self, user_id: &Uuid) -> GammaResult<Vec<GammaUserGroup>> {
        let request = self
            .client
            .get(format!("{}/v1/groups/for/{user_id}", self.gamma_url));

        let user_groups = self
            .handle_gamma_request(request, "get groups for user endpoint")
            .await?;

        Ok(user_groups)
    }

    /// Get all authorities for this client.
    pub async fn get_authorities(&self) -> GammaResult<Vec<String>> {
        let request = self
            .client
            .get(format!("{}/v1/authorities", self.gamma_url));

        let authorities = self
            .handle_gamma_request(request, "get authorities endpoint")
            .await?;

        Ok(authorities)
    }

    /// Get all authorities for the provided `user_id` and this client.
    pub async fn get_authorities_for_user(&self, user_id: &Uuid) -> GammaResult<Vec<String>> {
        let request = self
            .client
            .get(format!("{}/v1/authorities/for/{user_id}", self.gamma_url));

        let authorities = self
            .handle_gamma_request(request, "get authorities for user endpoint")
            .await?;

        Ok(authorities)
    }

    async fn handle_gamma_request<T>(
        &self,
        request: RequestBuilder,
        context: &str,
    ) -> GammaResult<T>
    where
        T: DeserializeOwned,
    {
        let response = request
            .header(
                AUTHORIZATION,
                format!("{PRE_SHARED} {}", self.gamma_api_key),
            )
            .send()
            .await
            .map_err(|err| GammaError::FailedSendingRequest {
                context: context.into(),
                err,
            })?;

        let status = response.status();
        if !status.is_success() {
            if status == 404 {
                return Err(GammaError::NotFoundResponse {
                    context: context.into(),
                });
            }

            let body_str = response
                .text()
                .await
                .unwrap_or("Failed to read response body".into());

            return Err(GammaError::ErrorResponse {
                context: context.into(),
                status,
                body_str,
            });
        }

        let body: T =
            response
                .json()
                .await
                .map_err(|err| GammaError::FailedToDeserializeResponse {
                    context: context.into(),
                    error: err,
                })?;

        Ok(body)
    }
}

/// A group in gamma (e.g. digIT'21).
#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaGroup {
    /// A unique identifier for the group.
    pub id: Uuid,
    /// The name of the group.
    pub name: String,
    /// The pretty name of the group.
    pub pretty_name: String,
    /// The supergroup this group belongs to (e.g. digIT).
    pub super_group: GammaSuperGroup,
}

/// A supergroup in gamma (e.g. digIT).
#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaSuperGroup {
    /// A unique identifier for the supergroup.
    pub id: Uuid,
    /// The name of the supergroup (e.g. "digit").
    pub name: String,
    /// The pretty name of the supergroup (e.g. "digIT").
    pub pretty_name: String,
    /// The type of supergroup this is.
    #[serde(rename = "type")]
    pub group_type: GammaSuperGroupType,
    /// The swedish description of the supergroup.
    pub sv_description: String,
    /// The english description of the supergroup.
    pub en_description: String,
}

/// A type of supergroup.
/// Note: In gamma these are generic strings and can be created and deleted through the GUI,
/// for this reason an `Other` option is provided as a cath all and the remaining options
/// are simply the ones currently in use at the time of writing.
#[derive(Debug, Clone, Serialize, Deserialize, PartialEq)]
#[serde(rename_all = "camelCase")]
pub enum GammaSuperGroupType {
    /// An alumni group (not active).
    Alumni,
    /// A committee within the IT divison.
    Committee,
    /// A society recognized by the IT division.
    Society,
    /// Functionary groups within the IT division (e.g. auditors).
    Functionaries,
    /// Gamma administrators.
    Admin,
    /// A type that of group that is not specificly supported by this client.
    #[serde(untagged)]
    Other(String),
}

/// A gamma user.
#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaUser {
    /// A unique identifier for the user.
    pub id: Uuid,
    /// The Chalmers ID for the person.
    pub cid: String,
    /// The IT nickname of the user.
    pub nick: String,
    /// The first name of the person.
    pub first_name: String,
    /// The surname of the person.
    pub last_name: String,
    /// Which year they were accepted to chalmers.
    pub acceptance_year: i32,
}

/// A post within a gamma group.
#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaPost {
    /// A unique identifier for the post.
    pub id: Uuid,
    /// A version of the post(?), not entirely sure tbh.
    pub version: i32,
    /// The swedish version of the name for this post (e.g. "Ordförande").
    pub sv_name: String,
    /// The english version of name the for this post (e.g. "Chairman").
    pub en_name: String,
}

/// Connection between a user and a group containing information about the group, its supergroup as well as which post the user held within that group.
#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaUserGroup {
    /// The group ID.
    pub id: Uuid,
    /// The name of the group (e.g. "digit22").
    pub name: String,
    /// A pretty name of the group (e.g. "digIT 22/23").
    pub pretty_name: String,
    /// The supergroup for this group (e.g. didIT).
    pub super_group: GammaSuperGroup,
    /// The post the user held within this group (e.g. treasurer).
    pub post: GammaPost,
}
