package it.chalmers.gamma.media

import java.util.UUID

// Gamma 2.5.1 compared the integer number of MiB with 2, so it accepted every image smaller than
// 3 MiB. Keep that exact persisted-client boundary even though the user-facing limit said 2 MiB.
const val MAX_MEDIA_BYTES: Int = 3 * 1024 * 1024 - 1

/**
 * A safe relative path previously persisted by Gamma.
 *
 * The 36-character prefix deliberately retains Gamma 2.5.1's identifier-shaped validation
 * instead of imposing a new UUID constraint on existing database values. The restricted alphabet,
 * optional single child name, and image extension keep the path inside media storage.
 */
@JvmInline
value class MediaUri(
    val value: String,
) {
    init {
        require(value.matches(STORED_MEDIA_URI_PATTERN)) { "Invalid media URI" }
    }

    override fun toString(): String = "MediaUri(<redacted>)"

    private companion object {
        val STORED_MEDIA_URI_PATTERN =
            Regex(
                "^[0-9a-f-]{36}(?:/[A-Za-z0-9_-][A-Za-z0-9._-]{0,254})?\\.(?:jpe?g|png|gif)$",
            )
    }
}

@JvmInline
value class MediaObjectId(
    val value: UUID,
) {
    companion object {
        fun generate(): MediaObjectId = MediaObjectId(UUID.randomUUID())
    }

    override fun toString(): String = "MediaObjectId(<redacted>)"
}

/**
 * Media bytes returned for transport to a caller.
 *
 * This is intentionally a reference-semantic class: [bytes] is a mutable transport buffer, not a
 * value suitable for generated data-class equality or hashing.
 */
class MediaContent(
    val bytes: ByteArray,
    val contentType: String,
)

enum class DefaultMedia {
    USER_AVATAR,
    GROUP_AVATAR,
    GROUP_BANNER,
}

interface MediaStore {
    fun save(
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri

    fun save(
        objectId: MediaObjectId,
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri

    fun read(
        uri: MediaUri?,
        fallback: DefaultMedia,
    ): MediaContent

    fun delete(uri: MediaUri)

    fun delete(objectId: MediaObjectId)
}

class InvalidMedia(
    message: String,
) : IllegalArgumentException(message)

class MediaTooLarge(
    message: String,
) : IllegalArgumentException(message)
