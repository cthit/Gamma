package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path

internal val groupAdministrator =
    Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), isAdministrator = true)
internal val ordinaryGroupUser =
    Actor.User(ActorUserId(UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195").value))
internal val existingGroupId = GroupId.parse("047ac437-a789-4cc5-bb6e-ba50efd7c509")
internal val existingSuperGroupId = SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb")
internal val groupMembership =
    NewGroupMembership(
        UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195"),
        PostId.parse("7bb1db15-730d-4864-bfc3-99abe7c0ccf8"),
        UnofficialPostName("Test member"),
    )

internal fun withGroupDatabase(test: (DatabaseFactory, OrganizationQueries) -> Unit) {
    val migrations =
        Path
            .of(checkNotNull(System.getProperty("gamma.root")))
            .resolve("app/src/main/resources/db/migration")
    PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
        .use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 3),
            ).use { database -> test(database, OrganizationQueries(database)) }
        }
}

// Real PostgreSQL authority for context tests; application tests cover UserAccountAccess composition.
internal fun organizationAccess(database: DatabaseFactory): OrganizationAccess =
    OrganizationAccess { transaction, actor ->
        database.requireTransaction(transaction)
        val user =
            actor as? Actor.User ?: throw it.chalmers.gamma.platform.core
                .AccessDenied()
        transaction.exec("LOCK TABLE g_admin_user IN SHARE MODE")
        transaction.exec(
            "SELECT EXISTS (SELECT 1 FROM g_admin_user WHERE user_id = '${user.userId.value}')",
        ) { result ->
            check(result.next())
            result.getBoolean(1)
        } ?: false
    }
