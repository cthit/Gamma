package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

@Configuration(proxyBeanMethods = false)
internal class MockDataBootstrap {
    @Bean
    fun mockDataBootstrapRunner(
        settings: AppSettings,
        database: DatabaseFactory,
        apiAccess: ApiKeyQueries,
        creation: CreateApiKey,
        objectMapper: ObjectMapper,
    ): ApplicationRunner =
        ApplicationRunner { _ ->
            if (!settings.mocking) return@ApplicationRunner
            val fixture = loadMockData(settings.mockDataResource, objectMapper)
            val sql = fixture.toSql(database, Instant.now())
            if (sql.isNotBlank()) database.executeSqlScript(sql)

            val existingNames =
                database.commitTransaction(readOnly = true) {
                    apiAccess.listApiKeysIn(this).mapTo(mutableSetOf()) { it.name }
                }
            ApiKeyType.entries.filterNot { it == ApiKeyType.CLIENT }.forEach { type ->
                val name = ApiKeyName("${type.name.lowercase()}-mock")
                if (name !in existingNames) {
                    val created = creation.create(name, LocalizedText.of(), type)
                    mockBootstrapLogger.info(
                        "Mock API key of type {} has been generated with id: {}",
                        type.name,
                        created.apiKey.id.value,
                    )
                }
            }
        }
}

private fun loadMockData(
    location: String,
    objectMapper: ObjectMapper,
): JsonNode {
    val stream =
        if (location.startsWith("classpath:")) {
            ClassPathResource(location.removePrefix("classpath:")).inputStream
        } else {
            java.nio.file.Files
                .newInputStream(
                    java.nio.file.Path
                        .of(location),
                )
        }
    return stream.use(objectMapper::readTree).also { fixture ->
        require(fixture.path("users").isArray) { "Mock data must contain users" }
        require(fixture.path("groups").isArray) { "Mock data must contain groups" }
        require(fixture.path("superGroups").isArray) { "Mock data must contain super groups" }
        require(fixture.path("posts").isArray) { "Mock data must contain posts" }
    }
}

private fun JsonNode.toSql(
    database: DatabaseFactory,
    now: Instant,
): String =
    buildString {
        if (database.tableRowCount("g_user") <= 1) appendUsers(this@toSql, now)
        if (database.tableRowCount("g_post") == 0L) appendPosts(this@toSql, now)
        if (database.tableRowCount("g_super_group") == 0L) appendSuperGroups(this@toSql, now)
        if (database.tableRowCount("g_group") == 0L) appendGroups(this@toSql, now)
    }

private fun StringBuilder.appendUsers(
    fixture: JsonNode,
    now: Instant,
) {
    appendInsert(
        "g_user",
        "user_id, cid, password, nick, first_name, last_name, email, language, " +
            "user_agreement_accepted, acceptance_year, version, locked, created_at, updated_at",
        fixture
            .path("users")
            .values()
            .map { user ->
                listOf<Any?>(
                    user.text("id"),
                    user.text("cid"),
                    MOCK_PASSWORD_HASH,
                    user.text("nick"),
                    user.text("firstName"),
                    user.text("lastName"),
                    "${user.text("cid")}@example.org",
                    "EN",
                    now,
                    user.path("acceptanceYear").asInt(),
                    0,
                    false,
                    now,
                    now,
                )
            },
        " ON CONFLICT DO NOTHING",
    )
}

private fun StringBuilder.appendPosts(
    fixture: JsonNode,
    now: Instant,
) {
    val posts = fixture.path("posts").toList()
    appendInsert(
        "g_text",
        "text_id, sv, en, created_at",
        posts.map {
            listOf(
                stableUuid("mock-post-name:${it.text("id")}"),
                it.path("postName").text("sv"),
                it.path("postName").text("en"),
                now,
            )
        },
    )
    appendInsert(
        "g_post",
        "post_id, post_name, email_prefix, version, created_at, updated_at, post_order",
        posts.mapIndexed { index, post ->
            listOf(post.text("id"), stableUuid("mock-post-name:${post.text("id")}"), "", 0, now, now, index)
        },
    )
}

private fun StringBuilder.appendSuperGroups(
    fixture: JsonNode,
    now: Instant,
) {
    val groups = fixture.path("superGroups").toList()
    appendInsert(
        "g_super_group_type",
        "super_group_type_name, created_at",
        groups.map { it.text("type").lowercase() }.distinct().map { listOf(it, now) },
        " ON CONFLICT DO NOTHING",
    )
    appendInsert(
        "g_text",
        "text_id, sv, en, created_at",
        groups.map { listOf(stableUuid("mock-super-group-description:${it.text("id")}"), "", "", now) },
    )
    appendInsert(
        "g_super_group",
        "super_group_id, e_name, pretty_name, super_group_type_name, created_at, updated_at, description, version",
        groups.map {
            listOf(
                it.text("id"),
                it.text("name"),
                it.text("prettyName"),
                it.text("type").lowercase(),
                now,
                now,
                stableUuid("mock-super-group-description:${it.text("id")}"),
                0,
            )
        },
    )
}

private fun StringBuilder.appendGroups(
    fixture: JsonNode,
    now: Instant,
) {
    val superGroups = fixture.path("superGroups").associateBy { it.text("id") }
    val groups = fixture.path("groups").toList()

    fun year(group: JsonNode): Int {
        val inactive = superGroups.getValue(group.text("superGroupId")).text("type").equals("alumni", true)
        return now.minus(if (inactive) 366 else 1, ChronoUnit.DAYS).atZone(ZoneOffset.UTC).year
    }
    appendInsert(
        "g_group",
        "group_id, e_name, pretty_name, super_group_id, created_at, updated_at, version",
        groups.map { group ->
            listOf(
                group.text("id"),
                group.text("name") + year(group),
                group.text("prettyName") + year(group),
                group.text("superGroupId"),
                now,
                now,
                0,
            )
        },
    )
    appendInsert(
        "g_membership",
        "created_at, user_id, group_id, post_id, unofficial_post_name",
        groups.flatMap { group ->
            group
                .path("members")
                .values()
                .map { member ->
                    val unofficialName =
                        member
                            .path("unofficialPostName")
                            .takeUnless { it.isMissingNode }
                            ?.takeUnless { it.isNull }
                            ?.asString()
                    listOf<Any?>(now, member.text("userId"), group.text("id"), member.text("postId"), unofficialName)
                }
        },
    )
}

private fun StringBuilder.appendInsert(
    table: String,
    columns: String,
    rows: List<List<Any?>>,
    conflict: String = "",
) {
    if (rows.isEmpty()) return
    append("INSERT INTO $table ($columns) VALUES\n")
    rows.forEachIndexed { index, row ->
        append(row.joinToString(prefix = "(", postfix = ")", transform = ::sqlValue))
        append(if (index == rows.lastIndex) "$conflict;\n" else ",\n")
    }
}

private fun sqlValue(value: Any?): String =
    when (value) {
        null -> "NULL"
        is Number -> value.toString()
        is Boolean -> value.toString().uppercase()
        else -> "'${value.toString().replace("'", "''")}'"
    }

private fun JsonNode.text(field: String): String = path(field).asString()

private fun stableUuid(value: String): UUID = UUID.nameUUIDFromBytes(value.toByteArray(StandardCharsets.UTF_8))

private val mockBootstrapLogger = LoggerFactory.getLogger("it.chalmers.gamma.Bootstrap")
private const val MOCK_PASSWORD_HASH =
    "{bcrypt}\$2y\$10\$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS"
