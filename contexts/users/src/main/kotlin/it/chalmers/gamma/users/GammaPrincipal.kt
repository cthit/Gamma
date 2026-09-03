package it.chalmers.gamma.users

import java.io.Serializable
import java.security.Principal

data class GammaPrincipal(
    val userId: String,
    val nick: String,
    val administrator: Boolean,
) : Principal,
    Serializable {
    override fun getName(): String = userId

    // Spring Authorization Server reconstructs the authorization principal after the
    // code exchange. The session user ID must remain stable when profile or access flags change.
    override fun equals(other: Any?): Boolean = other is GammaPrincipal && userId == other.userId

    override fun hashCode(): Int = userId.hashCode()

    override fun toString(): String = "GammaPrincipal(userId=<redacted>, nick=<redacted>, administrator=$administrator)"

    private companion object {
        const val serialVersionUID = 1L
    }
}
