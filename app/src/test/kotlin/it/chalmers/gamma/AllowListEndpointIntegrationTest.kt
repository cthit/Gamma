package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AllowListEndpointIntegrationTest
    @Autowired
    constructor(
        private val dataSource: DataSource,
    ) : SpringApplicationTest() {
        @Test
        fun `partial response never adds a rejected existing user to the allow list`() {
            assertFalse(isAllowed("mscott"))
            try {
                val response =
                    browser(uniqueAddress()).json(
                        "POST",
                        "/api/allow-list/v1",
                        """{"cids":["httpfirst","mscott","httplast"]}""",
                        mapOf("Authorization" to ALLOW_LIST_CREDENTIALS),
                    )

                assertEquals(206, response.status, response.body)
                assertEquals("""["mscott"]""", response.body)
                assertFalse(isAllowed("mscott"), "A CID reported as rejected must not be inserted")
                assertTrue(isAllowed("httpfirst"))
                assertTrue(isAllowed("httplast"))
            } finally {
                executeSql("DELETE FROM g_allow_list WHERE cid IN ('httpfirst', 'mscott', 'httplast')")
            }
        }

        @Test
        fun `partial response preserves accepted items on both sides of a SQL failure`() {
            executeSql(
                """
                CREATE FUNCTION reject_http_batch_cid() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF NEW.cid = 'httpbad' THEN
                        RAISE EXCEPTION 'forced allow-list HTTP failure';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_http_batch_cid BEFORE INSERT ON g_allow_list
                FOR EACH ROW EXECUTE FUNCTION reject_http_batch_cid();
                """.trimIndent(),
            )
            try {
                val response =
                    browser(uniqueAddress()).json(
                        "POST",
                        "/api/allow-list/v1",
                        """{"cids":["httpfirst","httpbad","httplast"]}""",
                        mapOf("Authorization" to ALLOW_LIST_CREDENTIALS),
                    )

                assertEquals(206, response.status, response.body)
                assertEquals("""["httpbad"]""", response.body)
                assertTrue(isAllowed("httpfirst"), "A later failure must not undo an accepted item")
                assertFalse(isAllowed("httpbad"))
                assertTrue(isAllowed("httplast"))
            } finally {
                executeSql(
                    """
                    DROP TRIGGER reject_http_batch_cid ON g_allow_list;
                    DROP FUNCTION reject_http_batch_cid();
                    DELETE FROM g_allow_list WHERE cid IN ('httpfirst', 'httpbad', 'httplast');
                    """.trimIndent(),
                )
            }
        }

        private fun isAllowed(cid: String): Boolean =
            dataSource.connection.use { connection ->
                connection.prepareStatement("SELECT 1 FROM g_allow_list WHERE cid = ?").use { statement ->
                    statement.setString(1, cid)
                    statement.executeQuery().use { it.next() }
                }
            }

        private fun executeSql(sql: String) {
            dataSource.connection.use { connection ->
                connection.createStatement().use { it.execute(sql) }
                if (!connection.autoCommit) connection.commit()
            }
        }

        private companion object {
            const val ALLOW_LIST_CREDENTIALS =
                "pre-shared 33333333-3333-4333-8333-333333333333:gamma-info-regression-token-000001"
        }
    }
