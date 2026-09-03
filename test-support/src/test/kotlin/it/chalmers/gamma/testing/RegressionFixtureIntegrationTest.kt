package it.chalmers.gamma.testing

import java.nio.file.Path
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RegressionFixtureIntegrationTest {
    @Test
    fun `source-tree migrations and classpath regression fixture load into PostgreSQL`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { database ->
                database.connection { connection ->
                    assertEquals(13, connection.count("g_user"))
                    assertEquals(9, connection.count("g_super_group"))
                    assertEquals(9, connection.count("g_group"))
                    assertEquals(4, connection.count("g_post"))
                    assertEquals(31, connection.count("g_membership"))
                    assertEquals(3, connection.count("g_api_key"))

                    connection
                        .prepareStatement(
                            "SELECT nick, email FROM g_user WHERE cid = 'mscott'",
                        ).use { statement ->
                            statement.executeQuery().use { rows ->
                                assertTrue(rows.next())
                                assertEquals("Boss", rows.getString("nick"))
                                assertEquals("mscott@example.org", rows.getString("email"))
                            }
                        }

                    connection
                        .prepareStatement(
                            "SELECT e_name FROM g_group ORDER BY e_name",
                        ).use { statement ->
                            statement.executeQuery().use { rows ->
                                val names =
                                    buildList {
                                        while (rows.next()) add(rows.getString("e_name"))
                                    }
                                assertTrue("digit2025" in names)
                                assertTrue("digit2026" in names)
                            }
                        }

                    assertFailsWith<SQLException> {
                        connection
                            .prepareStatement(
                                """
                                INSERT INTO g_membership
                                    (created_at, user_id, group_id, post_id, unofficial_post_name)
                                VALUES
                                    (NOW(), 'ffffffff-ffff-4fff-8fff-ffffffffffff',
                                     '047ac437-a789-4cc5-bb6e-ba50efd7c509',
                                     '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', NULL)
                                """.trimIndent(),
                            ).use { it.executeUpdate() }
                    }
                    connection.rollback()
                }
            }
    }

    private fun java.sql.Connection.count(table: String): Int =
        createStatement().use { statement ->
            statement.executeQuery("SELECT COUNT(*) FROM $table").use { rows ->
                rows.next()
                rows.getInt(1)
            }
        }
}
