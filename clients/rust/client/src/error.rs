/// An error that can occur within the library.
#[derive(Debug, thiserror::Error)]
pub enum GammaError {
    /// The state received from gamma did not match the state we sent to them.
    #[error("State sent to gamma did not match what we got in the callback")]
    GammaStateMissmatch,
    /// The callback from gamma did not contain the required code query parameter.
    #[error("The callback from gamma did not contain the expected code query parameter")]
    NoCodeReceived,
    /// Failed sending a request to gamma.
    #[error("Failed sending request to gamma in context {context:?}")]
    FailedSendingRequest {
        /// Which endpoint the request failed for.
        context: String,
        /// The error that occurred.
        #[source]
        err: reqwest::Error,
    },
    /// Got a non 2XX response from the gamma API.
    #[error("Got an error from gamma in context {context:?} status {status:?} body: {body_str:?}")]
    ErrorResponse {
        /// Which endpoint the request failed for.
        context: String,
        /// The error status returned.
        status: reqwest::StatusCode,
        /// The stringified response body.
        body_str: String,
    },
    /// The response body did not match the expected response body.
    #[error("Failed to deserialize gamma response in context {context:?}")]
    FailedToDeserializeResponse {
        /// The endpoint deserialization failed for.
        context: String,
        /// The error that occurred.
        #[source]
        error: reqwest::Error,
    },
    /// Got a 404 NOT_FOUND from gamma.
    #[error("Got a 404 Not Found response from gamma")]
    NotFoundResponse {
        /// The endpoint that returned 404.
        context: String,
    },
}

/// The result type used within this client.
pub type GammaResult<T> = Result<T, GammaError>;
