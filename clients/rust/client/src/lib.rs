#![forbid(unsafe_code)]
#![deny(missing_docs)]

//! # Gamma Rust client library

/// Endpoints and types used for the oauth flow.
pub mod oauth;

/// Configurations for the gamma api.
pub mod config;

/// Endpoints and types for the gamma client API.
#[cfg(feature = "api")]
pub mod api;

/// Error types used within the lib.
pub mod error;
