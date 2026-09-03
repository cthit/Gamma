package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import java.util.UUID

data class OAuthServerClient(
    val client: OAuthClient,
    val encodedSecret: String,
) {
    override fun toString(): String = "OAuthServerClient(client=$client, encodedSecret=<redacted>)"
}

class OAuthClientNotFound(
    message: String,
) : RuntimeException(message)

class OAuthClientConflict(
    message: String,
) : RuntimeException(message)
