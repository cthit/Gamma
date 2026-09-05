package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.UserQueries

data class OAuthProfileClaims(
    val cid: String,
    val givenName: String,
    val familyName: String,
    val nickname: String,
    val displayName: String,
    val locale: String,
    val pictureUserId: UserId,
)

data class AuthorizedOAuthClaims(
    val subject: String,
    val profile: OAuthProfileClaims?,
    val email: String?,
)

class OAuthClaimDecisions(
    private val users: UserQueries,
) {
    fun claims(
        userId: UserId,
        authorizedScopes: Set<String>,
    ): AuthorizedOAuthClaims? {
        val user = users.findUser(userId) ?: return null
        val profile =
            if (Scope.PROFILE.wireValue in authorizedScopes) {
                OAuthProfileClaims(
                    cid = user.cid.value,
                    givenName = user.firstName.value,
                    familyName = user.lastName.value,
                    nickname = user.nick.value,
                    displayName = "${user.firstName.value} '${user.nick.value}' ${user.lastName.value}",
                    locale = (user.language ?: Language.EN).name.lowercase(),
                    pictureUserId = user.id,
                )
            } else {
                null
            }
        val email = user.email.value.takeIf { Scope.EMAIL.wireValue in authorizedScopes }
        return AuthorizedOAuthClaims(user.id.value.toString(), profile, email)
    }
}
