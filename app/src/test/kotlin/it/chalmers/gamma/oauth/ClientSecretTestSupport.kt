package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.security.crypto.factory.PasswordEncoderFactories

/** Checks persisted credentials through the actual authorization-server projection. */
internal fun clientSecretMatches(
    database: DatabaseFactory,
    clientId: ClientId,
    secret: RawClientSecret,
): Boolean {
    val client = OAuthProtocolClients(database).serverClient(clientId) ?: return false
    return PasswordEncoderFactories.createDelegatingPasswordEncoder().matches(secret.value, client.encodedSecret)
}
