package it.chalmers.gamma.platform.core

import java.util.UUID

interface UserIdentifier

@JvmInline
value class UserId(
    val value: UUID,
) : UserIdentifier {
    companion object {
        fun parse(value: String): UserId = UserId(UUID.fromString(value))

        fun generate(): UserId = UserId(UUID.randomUUID())
    }

    override fun toString(): String = "UserId(<redacted>)"
}

@JvmInline
value class SuperGroupId(
    val value: UUID,
) {
    companion object {
        fun parse(value: String) = SuperGroupId(UUID.fromString(value))

        fun generate() = SuperGroupId(UUID.randomUUID())
    }
}

@JvmInline
value class GroupId(
    val value: UUID,
) {
    companion object {
        fun parse(value: String) = GroupId(UUID.fromString(value))

        fun generate() = GroupId(UUID.randomUUID())
    }
}

@JvmInline
value class PostId(
    val value: UUID,
) {
    companion object {
        fun parse(value: String) = PostId(UUID.fromString(value))

        fun generate() = PostId(UUID.randomUUID())
    }
}

@JvmInline
value class ClientUid(
    val value: UUID,
) {
    companion object {
        fun generate() = ClientUid(UUID.randomUUID())

        fun parse(value: String) = ClientUid(UUID.fromString(value))
    }
}

@JvmInline
value class ApiKeyId(
    val value: UUID,
) {
    companion object {
        fun parse(value: String) = ApiKeyId(UUID.fromString(value))

        fun generate() = ApiKeyId(UUID.randomUUID())
    }
}

@JvmInline
value class SuperGroupType(
    val value: String,
) {
    init {
        require(value.matches(Regex("^[a-z]{3,30}$"))) {
            "must be made using a-z, with length between 3-30"
        }
    }
}

@JvmInline
value class LocalizedTextValue(
    val value: String,
) {
    init {
        require(value.length <= 2048) { "Must be between 0 and 2048" }
        require(value.none { it in HTML_SPECIAL_CHARACTERS }) { "Cannot have illegal html characters" }
    }
}

data class LocalizedText(
    val sv: LocalizedTextValue,
    val en: LocalizedTextValue,
) {
    companion object {
        fun of(
            sv: String = "",
            en: String = "",
        ) = LocalizedText(
            LocalizedTextValue(sv),
            LocalizedTextValue(en),
        )
    }
}

private val HTML_SPECIAL_CHARACTERS = setOf('&', '<', '>', '"', '\'')
