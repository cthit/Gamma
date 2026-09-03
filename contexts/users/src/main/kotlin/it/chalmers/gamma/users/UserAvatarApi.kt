package it.chalmers.gamma.users

import java.util.UUID

class UserAvatarUpload(
    bytes: ByteArray,
    val declaredContentType: String?,
) {
    private val content = bytes.copyOf()

    val bytes: ByteArray
        get() = content.copyOf()
}

@JvmInline
value class UserAvatarOperationId(
    val value: UUID,
) {
    companion object {
        fun generate(): UserAvatarOperationId = UserAvatarOperationId(UUID.randomUUID())
    }

    override fun toString(): String = "UserAvatarOperationId(<redacted>)"
}

@JvmInline
value class StoredUserAvatar(
    val uri: String,
) {
    init {
        require(uri.isNotBlank())
        require(uri.length <= 255) { "Avatar URI is too long" }
    }

    override fun toString(): String = "StoredUserAvatar(<redacted>)"
}
