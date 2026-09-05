package it.chalmers.gamma

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.RegistrationToken
import org.jetbrains.exposed.v1.core.VarCharColumnType
import java.util.UUID

// Arrange a token for an HTTP operation without sending mail from its fixture.
internal fun DatabaseFactory.seedActivationForTest(cid: Cid): RegistrationToken {
    val token = RegistrationToken(UUID.randomUUID().toString().replace("-", ""))
    commitTransaction {
        exec(
            """
            INSERT INTO g_user_activation (cid, token, created_at)
            VALUES (?, ?, clock_timestamp() AT TIME ZONE 'UTC')
            ON CONFLICT (cid) DO UPDATE SET token = EXCLUDED.token, created_at = EXCLUDED.created_at
            """.trimIndent(),
            args = listOf(VarCharColumnType() to cid.value, VarCharColumnType() to token.value),
        )
    }
    return token
}
