package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId

private val ORGANIZATION_NAME_PATTERN = Regex("^[0-9a-z-]{3,30}$")
private val EMAIL_PREFIX_PATTERN = Regex("^$|^(?:\\w+|\\w+\\.\\w+)+$")

typealias SuperGroupId = it.chalmers.gamma.platform.core.SuperGroupId
typealias GroupId = it.chalmers.gamma.platform.core.GroupId
typealias PostId = it.chalmers.gamma.platform.core.PostId
typealias SuperGroupType = it.chalmers.gamma.platform.core.SuperGroupType
typealias LocalizedTextValue = it.chalmers.gamma.platform.core.LocalizedTextValue
typealias LocalizedText = it.chalmers.gamma.platform.core.LocalizedText

@JvmInline
value class OrganizationName(
    val value: String,
) {
    init {
        require(value.matches(ORGANIZATION_NAME_PATTERN)) {
            "Must be lowercase letters a - z, '-', digits and be of length between 3 - 30"
        }
    }
}

@JvmInline
value class PrettyName(
    val value: String,
) {
    init {
        require(value.length in 2..50) { "Must be between 2 and 50" }
        require(value.isSafeText()) { "Cannot have illegal html characters" }
    }
}

@JvmInline
value class EmailPrefix(
    val value: String,
) {
    init {
        require(value.matches(EMAIL_PREFIX_PATTERN)) {
            "Email prefix must contain words separated by dots"
        }
    }
}

@JvmInline
value class PostOrder(
    val value: Int,
) {
    init {
        require(value >= 0) { "order must be >= 0" }
    }
}

@JvmInline
value class UnofficialPostName(
    val value: String?,
) {
    init {
        require(value == null || (value.isNotEmpty() && value.length <= 50 && value.isSafeText())) {
            "Unofficial post name must be safe text with length between 1 and 50"
        }
    }
}

data class SuperGroup(
    val id: SuperGroupId,
    val version: Int,
    val name: OrganizationName,
    val prettyName: PrettyName,
    val type: SuperGroupType,
    val description: LocalizedText,
)

data class Group(
    val id: GroupId,
    val version: Int,
    val name: OrganizationName,
    val prettyName: PrettyName,
    val superGroup: SuperGroup,
    val avatarUri: String?,
    val bannerUri: String?,
)

data class Post(
    val id: PostId,
    val version: Int,
    val name: LocalizedText,
    val emailPrefix: EmailPrefix,
    val order: PostOrder,
)

data class Membership(
    val userId: UserId,
    val groupId: GroupId,
    val postId: PostId,
    val unofficialPostName: UnofficialPostName,
)

private fun String.isSafeText(): Boolean = none { it in HTML_SPECIAL_CHARACTERS }

private val HTML_SPECIAL_CHARACTERS = setOf('&', '<', '>', '"', '\'')
