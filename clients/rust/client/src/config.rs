/// Configuration parameters required for a gamma client.
#[derive(Debug, Clone)]
pub struct GammaConfig {
    /// Client ID for the gamma oauth client.
    pub gamma_client_id: String,
    /// Client Secret for the gamma oauth client.
    pub gamma_client_secret: String,
    /// The URI that was registered for the gamma client (i.e. where gamma should redirect to after
    /// successful auth).
    pub gamma_redirect_uri: String,
    /// The URL to gamma.
    pub gamma_url: String,
    /// The scopes that should be requested, space separated e.g. "openid profile".
    pub scopes: String,
    #[cfg(feature = "api")]
    /// The API key that should be used when requesting information from the API endpoints.
    pub gamma_api_key: String,
}
