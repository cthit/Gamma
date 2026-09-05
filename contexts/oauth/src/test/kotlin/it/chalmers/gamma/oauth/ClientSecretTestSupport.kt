package it.chalmers.gamma.oauth

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.database.DatabaseFactory

/** Checks persisted credentials through the actual authorization-server projection. */
internal fun clientSecretMatches(
    database: DatabaseFactory,
    clientId: ClientId,
    secret: RawClientSecret,
): Boolean {
    val client = OAuthProtocolClients(database).serverClient(clientId) ?: return false
    return BCrypt
        .verifyer()
        .verify(
            secret.value.toCharArray(),
            client.encodedSecret.removePrefix("{bcrypt}").toCharArray(),
        ).verified
}
