package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyStore
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.oauth.OAuthClientStore
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.session.FindByIndexNameSessionRepository
import java.io.IOException

class UserDeletionCascade(
    private val database: DatabaseFactory,
    private val clients: OAuthClientStore,
    private val apiKeys: ApiKeyStore,
    private val users: UserStore,
    private val sessions: FindByIndexNameSessionRepository<*>,
    private val media: MediaStore,
) {
    fun delete(userId: UserId) {
        val avatarUri =
            database.transaction {
                val ownedApiKeyIds = clients.deleteOwnedBy(userId).mapTo(mutableSetOf()) { ApiKeyId(it.value) }
                apiKeys.deleteOwnedBy(ownedApiKeyIds)
                users.deleteUser(userId)
            }

        sessions.findByPrincipalName(userId.value.toString()).keys.forEach(sessions::deleteById)
        avatarUri?.let { deleteAvatar(userId, it) }
    }

    private fun deleteAvatar(
        userId: UserId,
        avatarUri: String,
    ) {
        try {
            media.delete(MediaUri(avatarUri))
        } catch (_: IOException) {
            logger.warn("Avatar could not be deleted for user {}", userId.value)
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(UserDeletionCascade::class.java)
    }
}
