package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OAuthConsentAtomicityIntegrationTest {
    @Test
    fun `user locking and scope changes wait until consent has committed`() {
        for (lockUser in listOf(false, true)) {
            PostgresTestEnvironment().use { postgres ->
                var beforeApproval: (() -> Unit)? = null
                val interceptor =
                    object : StatementInterceptor {
                        override fun beforeExecution(
                            transaction: Transaction,
                            context: StatementContext,
                        ) {
                            if (context.statement.type == StatementType.INSERT) {
                                val change = beforeApproval
                                beforeApproval = null
                                change?.invoke()
                            }
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                    val service =
                        ApprovalOAuth2AuthorizationConsentService(
                            database,
                            UserAccountAccess(database),
                            ClientApprovals(database),
                        )
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Atomic consent"),
                                LocalizedText.of(),
                                true,
                                ClientOwner.Official,
                            ),
                        )
                    val created = database.commitTransaction { creation.insertIn(this, prepared) }
                    val consent =
                        OAuth2AuthorizationConsent
                            .withId(
                                created.client.uid.value
                                    .toString(),
                                USER_ID,
                            ).scope("openid")
                            .scope("profile")
                            .scope("email")
                            .build()
                    val worker = Executors.newSingleThreadExecutor()
                    var mutation: Future<*>? = null
                    try {
                        beforeApproval = {
                            val started = CountDownLatch(1)
                            mutation =
                                worker.submit {
                                    started.countDown()
                                    database.executeSqlScript(
                                        if (lockUser) {
                                            "UPDATE g_user SET locked = TRUE WHERE user_id = '$USER_ID'"
                                        } else {
                                            "DELETE FROM g_client_scope " +
                                                "WHERE client_uid = '${created.client.uid.value}' " +
                                                "AND scope = 'EMAIL'"
                                        },
                                    )
                                }
                            assertTrue(started.await(5, TimeUnit.SECONDS))
                            assertFailsWith<TimeoutException> { mutation?.get(1, TimeUnit.SECONDS) }
                        }
                        service.save(consent)
                        assertEquals(1, database.tableRowCount("g_user_approval"))
                    } finally {
                        mutation?.get(10, TimeUnit.SECONDS)
                        worker.shutdownNow()
                    }
                }
            }
        }
    }

    @Test
    fun `consent save rejects an enclosing transaction`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val service =
                    ApprovalOAuth2AuthorizationConsentService(
                        database,
                        UserAccountAccess(database),
                        ClientApprovals(database),
                    )
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared =
                    creation.prepare(
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("Consent boundary"),
                            LocalizedText.of(),
                            true,
                            ClientOwner.Official,
                        ),
                    )
                val created = database.commitTransaction { creation.insertIn(this, prepared) }
                val consent =
                    OAuth2AuthorizationConsent
                        .withId(
                            created.client.uid.value
                                .toString(),
                            USER_ID,
                        ).scope("openid")
                        .scope("profile")
                        .scope("email")
                        .build()
                database.commitTransaction { assertFailsWith<IllegalStateException> { service.save(consent) } }
            }
        }
    }

    @Test
    fun `unavailable accounts and incomplete scopes cannot grant or refresh consent`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val approvals = ClientApprovals(database)
                val service =
                    ApprovalOAuth2AuthorizationConsentService(database, UserAccountAccess(database), approvals)
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared =
                    creation.prepare(
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("Consent checks"),
                            LocalizedText.of(),
                            true,
                            ClientOwner.Official,
                        ),
                    )
                val created = database.commitTransaction { creation.insertIn(this, prepared) }
                val uid =
                    created.client.uid.value
                        .toString()
                val complete =
                    OAuth2AuthorizationConsent
                        .withId(uid, USER_ID)
                        .scope("openid")
                        .scope("profile")
                        .scope("email")
                        .build()
                assertFailsWith<IllegalArgumentException> {
                    service.save(OAuth2AuthorizationConsent.withId(uid, USER_ID).scope("openid").build())
                }
                assertFailsWith<IllegalArgumentException> {
                    service.save(OAuth2AuthorizationConsent.withId(uid, USER_ID).scope("admin").build())
                }
                assertFailsWith<IllegalArgumentException> {
                    service.save(
                        OAuth2AuthorizationConsent
                            .withId(
                                uid,
                                java.util.UUID
                                    .randomUUID()
                                    .toString(),
                            ).scope("openid")
                            .scope("profile")
                            .scope("email")
                            .build(),
                    )
                }
                assertEquals(0, database.tableRowCount("g_user_approval"))
                service.save(complete)
                service.save(complete)
                database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '$USER_ID'")
                assertFailsWith<IllegalArgumentException> { service.save(complete) }
                assertEquals(1, database.tableRowCount("g_user_approval"))
                assertNull(service.findById("not-a-client", USER_ID))
                assertNull(service.findById(uid, "not-a-user"))
                service.remove(complete)
                service.remove(complete)
                assertNull(service.findById(uid, USER_ID))
            }
        }
    }

    @Test
    fun `concurrent consent saves remain idempotent when one SQL attempt retries`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val service =
                    ApprovalOAuth2AuthorizationConsentService(
                        database,
                        UserAccountAccess(database),
                        ClientApprovals(database),
                    )
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared =
                    creation.prepare(
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("Concurrent consent"),
                            LocalizedText.of(),
                            true,
                            ClientOwner.Official,
                        ),
                    )
                val created = database.commitTransaction { creation.insertIn(this, prepared) }
                val consent =
                    OAuth2AuthorizationConsent
                        .withId(
                            created.client.uid.value
                                .toString(),
                            USER_ID,
                        ).scope("openid")
                        .scope("profile")
                        .scope("email")
                        .build()
                database.executeSqlScript(
                    """
                    CREATE SEQUENCE approval_attempts;
                    CREATE FUNCTION retry_approval_once() RETURNS trigger LANGUAGE plpgsql AS $$
                    BEGIN
                        IF nextval('approval_attempts') = 1 THEN RAISE EXCEPTION 'retry approval'; END IF;
                        RETURN NEW;
                    END $$;
                    CREATE TRIGGER retry_approval_once BEFORE INSERT ON g_user_approval
                    FOR EACH ROW EXECUTE FUNCTION retry_approval_once();
                    """.trimIndent(),
                )
                val workers = Executors.newFixedThreadPool(2)
                val start = CountDownLatch(1)
                try {
                    val requests =
                        List(2) {
                            workers.submit {
                                start.await()
                                service.save(consent)
                            }
                        }
                    start.countDown()
                    requests.forEach { it.get(10, TimeUnit.SECONDS) }
                    assertEquals(1, database.tableRowCount("g_user_approval"))
                    assertEquals(
                        consent.scopes,
                        service
                            .findById(
                                created.client.uid.value
                                    .toString(),
                                USER_ID,
                            )?.scopes,
                    )
                } finally {
                    start.countDown()
                    workers.shutdownNow()
                }
            }
        }
    }

    private companion object {
        const val USER_ID = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"
    }
}
