package it.chalmers.gamma.users

typealias UserId = it.chalmers.gamma.platform.core.UserId
typealias UserIdentifier = it.chalmers.gamma.platform.core.UserIdentifier

@JvmInline
value class Cid(
    val value: String,
) : UserIdentifier {
    init {
        require(value.matches(Regex("^[a-z]{4,12}$"))) {
            "Cid length must be between 4 and 12, and only have letters between a - z"
        }
    }

    override fun toString(): String = "Cid(<redacted>)"
}

@JvmInline
value class Email(
    val value: String,
) : UserIdentifier {
    init {
        require(value.matches(EMAIL_PATTERN)) { "Does not look like a valid email" }
        require(value.hasNoHtmlSensitiveCharacters()) { "Cannot have illegal html characters" }
        require(value.length <= 100) { "Must be at most 100 characters" }
    }

    override fun toString(): String = "Email(<redacted>)"

    private companion object {
        val EMAIL_PATTERN =
            Regex(
                "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            )
    }
}

@JvmInline
value class Nick(
    val value: String,
) {
    init {
        require(value.isNotEmpty()) { "Cannot be empty" }
        require(value.hasNoHtmlSensitiveCharacters()) { "Cannot have illegal html characters" }
        require(value.length <= 50) { "Must be between 1 and 50" }
    }

    override fun toString(): String = "Nick(<redacted>)"
}

@JvmInline
value class FirstName(
    val value: String,
) {
    init {
        require(value.isNotEmpty()) { "Cannot be empty" }
        require(value.hasNoHtmlSensitiveCharacters()) { "Cannot have illegal html characters" }
        require(value.length <= 50) { "Must be between 1 and 50" }
    }

    override fun toString(): String = "FirstName(<redacted>)"
}

@JvmInline
value class LastName(
    val value: String,
) {
    init {
        require(value.isNotEmpty()) { "Cannot be empty" }
        require(value.hasNoHtmlSensitiveCharacters()) { "Cannot have illegal html characters" }
        require(value.length <= 50) { "Must be between 1 and 50" }
    }

    override fun toString(): String = "LastName(<redacted>)"
}

@JvmInline
value class AcceptanceYear private constructor(
    val value: Int,
) {
    companion object {
        fun of(
            value: Int,
            currentYear: Int,
        ): AcceptanceYear {
            require(value in 2001..currentYear) {
                "Acceptance year must be between 2001 and current year"
            }
            return AcceptanceYear(value)
        }
    }

    override fun toString(): String = "AcceptanceYear(<redacted>)"
}

enum class Language {
    SV,
    EN,
}

@JvmInline
value class PasswordHash(
    val value: String,
) {
    init {
        require(value.startsWith("{bcrypt}$")) { "Only bcrypt password hashes are supported" }
    }

    override fun toString(): String = "<value redacted>"
}

@JvmInline
value class PlainTextPassword(
    val value: String,
) {
    init {
        require(value.length >= 12) { "Must be at least 12" }
    }

    override fun toString(): String = "<value redacted>"
}

data class UserProfile(
    val id: UserId,
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val language: Language?,
    val email: Email,
    val version: Int,
    val locked: Boolean,
    val avatarUri: String?,
) {
    init {
        require(version >= 0) { "Version cannot be negative" }
        require(avatarUri == null || avatarUri.length <= 255) { "Avatar URI is too long" }
    }

    override fun toString(): String = "UserProfile(<redacted>)"
}

private fun String.hasNoHtmlSensitiveCharacters(): Boolean = none { it in HTML_SENSITIVE_CHARACTERS }

private val HTML_SENSITIVE_CHARACTERS = setOf('&', '<', '>', '"', '\'')
