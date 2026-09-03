package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.platform.database.databaseUnitOfWork
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class OrganizationAdministrationIntegrationTest {
    @Test
    fun `only an administrator can mutate organization data`() =
        withAdministration {
            val before = queries.listPosts()

            assertFailsWith<AccessDenied> {
                administration.createPost(
                    ordinaryUser,
                    NewPost(LocalizedText.of("Nekad", "Denied"), EmailPrefix("denied")),
                )
            }
            assertEquals(before, queries.listPosts())

            administration.createPost(
                administrator,
                NewPost(LocalizedText.of("Tillåten", "Allowed"), EmailPrefix("allowed")),
            )
            assertEquals(before.size + 1, queries.listPosts().size)
        }

    @Test
    fun `group edit transaction rolls back metadata when memberships fail`() =
        withAdministration {
            val groupId = GroupId.parse(DIGIT_GROUP)
            val original = assertNotNull(queries.findGroup(groupId))
            val memberships = queries.membershipsForGroup(groupId)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_membership_replacement() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF OLD.group_id = '$DIGIT_GROUP'::UUID THEN
                        RAISE EXCEPTION 'forced membership persistence failure';
                    END IF;
                    RETURN OLD;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_membership_replacement
                    BEFORE DELETE ON g_membership
                    FOR EACH ROW EXECUTE FUNCTION reject_membership_replacement();
                """.trimIndent(),
            )

            assertFails {
                administration.updateGroup(
                    administrator,
                    original.copy(
                        name = OrganizationName("atomic-group"),
                        prettyName = PrettyName("Atomic group"),
                    ),
                    memberships,
                )
            }
            assertEquals(original, queries.findGroup(groupId))
            assertEquals(memberships, queries.membershipsForGroup(groupId))
        }

    private fun withAdministration(test: AdministrationFixture.() -> Unit) {
        val migrations =
            Path
                .of(checkNotNull(System.getProperty("gamma.root")))
                .resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 3),
                ).use { database ->
                    val queries = OrganizationStore(database)
                    val administration =
                        OrganizationAdministration(
                            queries,
                            databaseUnitOfWork(database),
                        )
                    run {
                        AdministrationFixture(
                            administration,
                            queries,
                            database,
                            Actor.User(ActorUserId(ADMINISTRATOR_ID.value), isAdministrator = true),
                            Actor.User(ActorUserId(ORDINARY_USER_ID.value)),
                        ).test()
                    }
                }
            }
    }

    private class AdministrationFixture(
        val administration: OrganizationAdministration,
        val queries: OrganizationStore,
        val database: DatabaseFactory,
        val administrator: Actor.User,
        val ordinaryUser: Actor.User,
    )

    private companion object {
        val ADMINISTRATOR_ID = UserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"))
        val ORDINARY_USER_ID = UserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195"))
        const val DIGIT_GROUP = "047ac437-a789-4cc5-bb6e-ba50efd7c509"
    }
}
