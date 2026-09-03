package it.chalmers.gamma.platform.core

import java.util.UUID

sealed interface Actor {
    data object Anonymous : Actor

    data class User(
        val userId: ActorUserId,
        val isAdministrator: Boolean = false,
    ) : Actor {
        override fun toString(): String = "Actor.User(userId=<redacted>, isAdministrator=$isAdministrator)"
    }

    data class ApiClient(
        val credentialId: ApiClientCredentialId,
    ) : Actor {
        override fun toString(): String = "Actor.ApiClient(credentialId=<redacted>)"
    }
}

@JvmInline
value class ActorUserId(
    val value: UUID,
) {
    override fun toString(): String = "ActorUserId(<redacted>)"
}

@JvmInline
value class ApiClientCredentialId(
    val value: UUID,
) {
    override fun toString(): String = "ApiClientCredentialId(<redacted>)"
}

class AccessDenied : RuntimeException("Access denied")
