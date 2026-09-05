package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestApiIntegrationTest
    @Autowired
    constructor(
        private val dataSource: DataSource,
        private val statements: TransactionStatementCounter,
    ) : SpringApplicationTest() {
        @Test
        fun `info user query count does not grow with memberships`() {
            val browser = browser(uniqueAddress())

            val oneMembership =
                requestStatementCount(browser, "/api/info/v1/users/$MICHAEL_USER_ID", INFO_CREDENTIALS)
            val threeMemberships =
                requestStatementCount(browser, "/api/info/v1/users/$ANGELA_USER_ID", INFO_CREDENTIALS)

            assertEquals(oneMembership, threeMemberships)
            assertTrue(
                oneMembership <= INFO_USER_STATEMENT_LIMIT,
                "Expected at most $INFO_USER_STATEMENT_LIMIT statements, observed $oneMembership",
            )
        }

        @Test
        fun `client authority query count does not grow with memberships`() {
            installClientApiFixture()
            try {
                val browser = browser(uniqueAddress())

                val oneMembership =
                    requestStatementCount(
                        browser,
                        "/api/client/v1/authorities/for/$MICHAEL_USER_ID",
                        CLIENT_CREDENTIALS,
                    )
                val threeMemberships =
                    requestStatementCount(
                        browser,
                        "/api/client/v1/authorities/for/$ANGELA_USER_ID",
                        CLIENT_CREDENTIALS,
                    )

                assertEquals(oneMembership, threeMemberships)
                assertTrue(
                    oneMembership <= CLIENT_AUTHORITIES_STATEMENT_LIMIT,
                    "Expected at most $CLIENT_AUTHORITIES_STATEMENT_LIMIT statements, observed $oneMembership",
                )
            } finally {
                removeClientApiFixture()
            }
        }

        private fun requestStatementCount(
            browser: TestBrowser,
            path: String,
            credentials: String,
        ): Int {
            val (response, transactionStatementCounts) =
                statements.record {
                    browser.get(path, mapOf("Authorization" to credentials))
                }
            assertEquals(200, response.status, response.body)
            return transactionStatementCounts.maxOrNull() ?: error("The request did not open a database transaction")
        }

        private fun installClientApiFixture() {
            executeFixtureSql(CLIENT_API_FIXTURE)
        }

        private fun removeClientApiFixture() = executeFixtureSql(REMOVE_CLIENT_API_FIXTURE)

        private fun executeFixtureSql(sql: String) {
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement -> statement.execute(sql) }
                if (!connection.autoCommit) connection.commit()
            }
        }

        private companion object {
            const val MICHAEL_USER_ID = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"
            const val ANGELA_USER_ID = "858e5acc-c289-40d3-9422-d6d317f40299"
            const val INFO_CREDENTIALS =
                "pre-shared 11111111-1111-4111-8111-111111111111:gamma-info-regression-token-000001"
            const val CLIENT_CREDENTIALS =
                "pre-shared 44444444-4444-4444-8444-444444444444:gamma-info-regression-token-000001"
            const val INFO_USER_STATEMENT_LIMIT = 9
            const val CLIENT_AUTHORITIES_STATEMENT_LIMIT = 10
            const val REMOVE_CLIENT_API_FIXTURE = """
            DELETE FROM g_client WHERE client_uid = '55555555-5555-4555-8555-555555555555';
            DELETE FROM g_api_key WHERE api_key_id = '44444444-4444-4444-8444-444444444444';
            DELETE FROM g_text WHERE text_id = '44444444-0000-4000-8000-000000000001';
        """
            const val CLIENT_API_FIXTURE = """
            INSERT INTO g_text (text_id, sv, en, created_at)
            VALUES ('44444444-0000-4000-8000-000000000001', '', '', '2026-01-01T00:00:00Z')
            ON CONFLICT DO NOTHING;

            INSERT INTO g_api_key (
                api_key_id, pretty_name, token, key_type, created_at, updated_at, version, description
            ) VALUES (
                '44444444-4444-4444-8444-444444444444',
                'client-statement-count',
                '{bcrypt}${'$'}2y${'$'}10${'$'}43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu',
                'CLIENT',
                '2026-01-01T00:00:00Z',
                '2026-01-01T00:00:00Z',
                0,
                '44444444-0000-4000-8000-000000000001'
            ) ON CONFLICT DO NOTHING;

            INSERT INTO g_client (
                client_uid, client_id, client_secret, redirect_uri, pretty_name,
                created_at, description, official, created_by
            ) VALUES (
                '55555555-5555-4555-8555-555555555555',
                'STATEMENTCOUNTCLIENT0000000000',
                '{bcrypt}${'$'}2y${'$'}10${'$'}43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu',
                'https://example.org/callback',
                'Statement Count',
                '2026-01-01T00:00:00Z',
                '44444444-0000-4000-8000-000000000001',
                TRUE,
                NULL
            ) ON CONFLICT DO NOTHING;

            INSERT INTO g_client_api_key (created_at, client_uid, api_key_id)
            VALUES (
                '2026-01-01T00:00:00Z',
                '55555555-5555-4555-8555-555555555555',
                '44444444-4444-4444-8444-444444444444'
            ) ON CONFLICT DO NOTHING;

            INSERT INTO g_client_authority (created_at, client_uid, authority_name)
            VALUES (
                '2026-01-01T00:00:00Z',
                '55555555-5555-4555-8555-555555555555',
                'member'
            ) ON CONFLICT DO NOTHING;

            INSERT INTO g_client_authority_super_group (
                created_at, super_group_id, client_uid, authority_name
            ) VALUES (
                '2026-01-01T00:00:00Z',
                'aed27030-ad90-4526-855c-1e909b1dcecb',
                '55555555-5555-4555-8555-555555555555',
                'member'
            ) ON CONFLICT DO NOTHING;
        """
        }
    }
