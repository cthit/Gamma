#![forbid(unsafe_code)]
#![deny(missing_docs)]

//! # Gamma Rust client library

mod config;
mod error;
pub mod oauth;

#[cfg(feature = "api")]
pub mod api;
