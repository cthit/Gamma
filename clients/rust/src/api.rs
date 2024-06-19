use reqwest::{header::AUTHORIZATION, Client, RequestBuilder, Response};
use serde::{de::DeserializeOwned, Deserialize, Serialize};
use uuid::Uuid;

use crate::{
    config::GammaConfig,
    error::{GammaError, GammaResult},
};

const PRE_SHARED: &str = "pre-shared";

pub struct GammaClient {
    client: Client,
    gamma_url: String,
    gamma_api_key: String,
}

impl GammaClient {
    pub fn new(config: &GammaConfig) -> Self {
        Self {
            client: Client::new(),
            gamma_url: format!("{}/api", config.gamma_url),
            gamma_api_key: config.gamma_api_key.clone(),
        }
    }

    pub async fn get_groups(&self) -> GammaResult<Vec<GammaGroup>> {
        let request = self.client.get(&format!("{}/groups", self.gamma_url));

        let groups: Vec<GammaGroup> = self
            .handle_gamma_request(request, "get groups endpoint")
            .await?;

        Ok(groups)
    }

    pub async fn get_super_groups(&self) -> GammaResult<Vec<GammaSuperGroup>> {
        let request = self.client.get(&format!("{}/superGroups", self.gamma_url));

        let super_groups: Vec<GammaSuperGroup> = self
            .handle_gamma_request(request, "get supergroups endpoint")
            .await?;

        Ok(super_groups)
    }

    pub async fn get_users(&self) -> GammaResult<Vec<GammaUser>> {
        let request = self.client.get(&format!("{}/users", self.gamma_url));

        let users = self
            .handle_gamma_request(request, "get users endpoint")
            .await?;

        Ok(users)
    }

    pub async fn get_groups_for_user(&self, user_id: Uuid) -> GammaResult<Vec<GammaUserGroup>> {
        let request = self
            .client
            .get(&format!("{}/groups/for/{user_id}", self.gamma_url));

        let user_groups = self
            .handle_gamma_request(request, "get groups for user endpoint")
            .await?;

        Ok(user_groups)
    }

    pub async fn get_authorities(&self) -> GammaResult<Vec<String>> {
        let request = self.client.get(&format!("{}/authorities", self.gamma_url));

        let authorities = self
            .handle_gamma_request(request, "get authorities endpoint")
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

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaGroup {
    pub id: Uuid,
    pub cid: String,
    pub nick: String,
    pub first_name: String,
    pub last_name: String,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaSuperGroup {
    pub id: Uuid,
    pub name: String,
    pub pretty_name: String,
    pub group_type: GammaSuperGroupType,
    pub sv_description: String,
    pub en_description: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
// Note: These are not stable as they are treated as changeable strings in gamma.
pub enum GammaSuperGroupType {
    Alumni,
    Committee,
    Society,
    Functionaries,
    Admin,
    Other(String),
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaUser {
    pub id: Uuid,
    pub cid: String,
    pub nick: String,
    pub first_name: String,
    pub last_name: String,
    pub acceptance_year: i32,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaPost {
    pub id: Uuid,
    pub version: i32,
    pub sv_name: String,
    pub en_name: String,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct GammaUserGroup {
    pub id: Uuid,
    pub name: String,
    pub pretty_name: String,
    pub super_group: GammaSuperGroup,
    pub post: GammaPost,
}
