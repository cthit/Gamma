package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ClientApprovalsIntegrationTest {
    @Test
    fun `approval participates in caller rollback and rejects foreign or completed transactions`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val created = create(database)
                    val approvals = ClientApprovals(database)
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            approvals.approveIn(this, userId, created.uid, created.scopes)
                            throw IllegalArgumentException("later participant rejected approval")
                        }
                    }
                    assertNull(approvals.approvedScopes(userId, created.uid))
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> {
                            approvals.approveIn(this, userId, created.uid, created.scopes)
                        }
                    }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> {
                        approvals.approveIn(completed, userId, created.uid, created.scopes)
                    }
                    database.commitTransaction {
                        assertFailsWith<IllegalStateException> { approvals.approvedScopes(userId, created.uid) }
                        assertFailsWith<IllegalStateException> { approvals.revoke(userId, created.uid) }
                    }
                }
            }
        }
    }

    @Test
    fun `revocation is idempotent and only removes the requested account approval`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val created = create(database)
                val approvals = ClientApprovals(database)
                database.commitTransaction {
                    approvals.approveIn(this, userId, created.uid, created.scopes)
                    approvals.approveIn(this, otherUserId, created.uid, created.scopes)
                    approvals.approveIn(this, userId, created.uid, created.scopes)
                }
                assertEquals(2, database.tableRowCount("g_user_approval"))
                approvals.revoke(userId, created.uid)
                approvals.revoke(userId, created.uid)
                approvals.revoke(userId, ClientUid.generate())
                assertNull(approvals.approvedScopes(userId, created.uid))
                assertEquals(created.scopes, approvals.approvedScopes(otherUserId, created.uid))
                database.executeSqlScript("DELETE FROM g_client_scope WHERE client_uid = '${created.uid.value}'")
                assertEquals(setOf(Scope.OPENID), approvals.approvedScopes(otherUserId, created.uid))
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction { approvals.approveIn(this, otherUserId, created.uid, created.scopes) }
                }
                database.executeSqlScript(
                    "UPDATE g_client SET client_id = NULL WHERE client_uid = '${created.uid.value}'",
                )
                assertNull(approvals.approvedScopes(otherUserId, created.uid))
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction { approvals.approveIn(this, userId, created.uid, setOf(Scope.OPENID)) }
                }
            }
        }
    }

    @Test
    fun `skipped approval insertion and revocation cannot report success`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val created = create(database)
                val approvals = ClientApprovals(database)
                database.executeSqlScript(
                    """
                    CREATE FUNCTION skip_approval_write() RETURNS trigger LANGUAGE plpgsql AS $$
                    BEGIN RETURN NULL; END $$;
                    CREATE TRIGGER skip_approval_insert BEFORE INSERT ON g_user_approval
                    FOR EACH ROW EXECUTE FUNCTION skip_approval_write();
                    """.trimIndent(),
                )
                assertFailsWith<IllegalStateException> {
                    database.commitTransaction { approvals.approveIn(this, userId, created.uid, created.scopes) }
                }
                assertNull(approvals.approvedScopes(userId, created.uid))
                database.executeSqlScript("DROP TRIGGER skip_approval_insert ON g_user_approval")
                database.commitTransaction { approvals.approveIn(this, userId, created.uid, created.scopes) }
                database.executeSqlScript(
                    """
                    CREATE TRIGGER skip_approval_delete BEFORE DELETE ON g_user_approval
                    FOR EACH ROW EXECUTE FUNCTION skip_approval_write();
                    """.trimIndent(),
                )
                assertFailsWith<IllegalStateException> { approvals.revoke(userId, created.uid) }
                assertEquals(created.scopes, approvals.approvedScopes(userId, created.uid))
            }
        }
    }

    @Test
    fun `approval existence and scopes use the same read snapshot`() {
        PostgresTestEnvironment().use { postgres ->
            var secondRead: (() -> Unit)? = null
            var reads = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (secondRead != null && ++reads == 2) {
                            val mutation = secondRead
                            secondRead = null
                            mutation?.invoke()
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val created = create(database)
                val approvals = ClientApprovals(database)
                database.commitTransaction { approvals.approveIn(this, userId, created.uid, created.scopes) }
                reads = 0
                secondRead = {
                    database.executeSqlScript(
                        """
                        DELETE FROM g_user_approval WHERE client_uid = '${created.uid.value}';
                        DELETE FROM g_client_scope WHERE client_uid = '${created.uid.value}';
                        """.trimIndent(),
                    )
                }
                assertEquals(created.scopes, approvals.approvedScopes(userId, created.uid))
                assertNull(approvals.approvedScopes(userId, created.uid))
            }
        }
    }

    private fun create(database: DatabaseFactory): OAuthClient {
        val creation = CreateClient(database, bcryptCost = 10)
        val prepared =
            creation.prepare(
                NewOAuthClient(
                    RedirectUri("https://example.org/callback"),
                    ClientName("Approval test"),
                    LocalizedText.of(),
                    true,
                    ClientOwner.Official,
                ),
            )
        return database.commitTransaction { creation.insertIn(this, prepared).client }
    }

    private companion object {
        val userId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val otherUserId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
    }
}
