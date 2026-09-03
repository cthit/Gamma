package it.chalmers.gamma.organization

import java.util.UUID

enum class GroupImageKind {
    AVATAR,
    BANNER,
}

data class GroupImageUpload(
    val bytes: ByteArray,
    val declaredContentType: String?,
)

@JvmInline
value class GroupImageOperationId(
    val value: UUID,
) {
    companion object {
        fun generate(): GroupImageOperationId = GroupImageOperationId(UUID.randomUUID())
    }

    override fun toString(): String = "GroupImageOperationId(<redacted>)"
}

@JvmInline
value class StoredGroupImage(
    val uri: String,
) {
    init {
        require(uri.isNotBlank())
    }

    override fun toString(): String = "StoredGroupImage(<redacted>)"
}
