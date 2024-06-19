#[derive(Debug, thiserror::Error)]
pub enum GammaError {
    /// The state received from gamma did not match the state we sent to them.
    #[error("State sent to gamma did not match what we got in the callback")]
    GammaStateMissmatch,
    #[error("The callback from gamma did not contain the expected code query parameter")]
    NoCodeReceived,
    #[error("Failed sending request to gamma in context {context:?} due to error: {err:?}")]
    FailedSendingRequest {
        context: String,
        err: reqwest::Error,
    },
    #[error("Got an error from gamma in context {context:?} status {status:?} body: {body_str:?}")]
    ErrorResponse {
        context: String,
        status: reqwest::StatusCode,
        body_str: String,
    },
    #[error("Failed to deserialize gamma response in context {context:?} due to error: {error:?}")]
    FailedToDeserializeResponse {
        context: String,
        error: reqwest::Error,
    },
    #[error("Got a 404 Not Found response from gamma")]
    NotFoundResponse { context: String },
}

pub type GammaResult<T> = Result<T, GammaError>;
