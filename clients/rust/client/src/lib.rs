#![forbid(unsafe_code)]
#![deny(missing_docs)]

//! # Gamma Rust client library
//!
//! A client for the Gamma Auth service for the Rust programming language.
//! The library consists of two, mainly separate, parts.
//!  - The [oauth] part for performing oauth2 login with gamma and retrieving information through its Open ID Connect API.
//!  - The [api] part for interacting with the client API using a client API key.
//!
//! ## Usage example for the [oauth] part
//!
//! ```rust
//! use gamma_rust_client::config::GammaConfig;
//! use gamma_rust_client::error::GammaResult;
//! use gamma_rust_client::oauth::{gamma_init_auth, GammaState};
//!
//! # fn begin_auth(gamma_config: &GammaConfig) -> GammaResult<()> {
//! let init = gamma_init_auth(gamma_config)?;
//! // Redirect the user to `init.redirect_to` and store `init.state` for later.
//! # Ok(())
//! # }
//!
//! // User authorizes and we get a code back.
//!
//! # async fn retrieve_auth_token(gamma_config: &GammaConfig, stored_state: GammaState, response_state: &str, response_code: String) -> GammaResult<()> {
//! let access_token = stored_state.gamma_callback_params(gamma_config, response_state, response_code).await?;
//! let user = access_token.get_current_user(gamma_config).await?;
//! # Ok(())
//! # }
//! ```
//!
//! ## Usage example for the [api] part.
//!
//! ```rust
//! use gamma_rust_client::config::GammaConfig;
//! use gamma_rust_client::error::GammaResult;
//! use gamma_rust_client::api::GammaClient;
//!
//! # async fn use_api(gamma_config: &GammaConfig) -> GammaResult<()> {
//! let client = GammaClient::new(gamma_config);
//!
//! client.get_groups().await?;
//! # Ok(())
//! # }
//! ```

/// Configurations for the gamma api.
pub mod config;

/// Endpoints and types for the gamma client API.
#[cfg(feature = "api")]
pub mod api;

/// Endpoints and types used for the oauth flow.
#[cfg(feature = "oauth")]
pub mod oauth;

/// Error types used within the lib.
pub mod error;
