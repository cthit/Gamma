package it.chalmers.gamma.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class MediaTypesTest {
    @Test
    fun `media size preserves the legacy compatibility boundary`() {
        assertEquals(3_145_727, MAX_MEDIA_BYTES)
    }

    @Test
    fun `media uris accept generated and nested image paths`() {
        assertEquals(
            "123e4567-e89b-12d3-a456-426614174000.png",
            MediaUri("123e4567-e89b-12d3-a456-426614174000.png").value,
        )
        assertEquals(
            "123e4567-e89b-12d3-a456-426614174000/avatar_2.jpeg",
            MediaUri("123e4567-e89b-12d3-a456-426614174000/avatar_2.jpeg").value,
        )
    }

    @Test
    fun `media uris preserve non uuid legacy prefixes`() {
        val nonUuidPrefix = "a".repeat(36)

        assertEquals(
            "$nonUuidPrefix.gif",
            MediaUri("$nonUuidPrefix.gif").value,
        )
    }

    @Test
    fun `media uri child names preserve the accepted alphabet and length boundary`() {
        val prefix = "123e4567-e89b-12d3-a456-426614174000"
        val allowedCharacters = "Aa0_-.z"
        val maximumLengthChild = "a".repeat(255)

        assertEquals(
            "$prefix/$allowedCharacters.png",
            MediaUri("$prefix/$allowedCharacters.png").value,
        )
        assertEquals(
            "$prefix/$maximumLengthChild.png",
            MediaUri("$prefix/$maximumLengthChild.png").value,
        )
        listOf(
            "$prefix/${"a".repeat(256)}.png",
            "$prefix/avatar+2.png",
            "$prefix/.hidden.png",
            "$prefix/path/to.png",
        ).forEach { value -> assertFailsWith<IllegalArgumentException>(value) { MediaUri(value) } }
    }

    @Test
    fun `media uris reject traversal unsupported types deep paths and short prefixes`() {
        listOf(
            "../../outside.png",
            "123e4567-e89b-12d3-a456-426614174000.svg",
            "123e4567-e89b-12d3-a456-426614174000/path/to.png",
            "not-a-uuid.png",
        ).forEach { value -> assertFailsWith<IllegalArgumentException>(value) { MediaUri(value) } }
    }

    @Test
    fun `media uri diagnostics do not expose storage identifiers`() {
        val identifier = "123e4567-e89b-12d3-a456-426614174000/private-avatar.png"

        assertEquals("MediaUri(<redacted>)", MediaUri(identifier).toString())
    }

    @Test
    fun `generated media object ids are random uuids with redacted diagnostics`() {
        val objectId = MediaObjectId.generate()

        assertEquals(4, objectId.value.version())
        assertEquals(2, objectId.value.variant())
        assertEquals("MediaObjectId(<redacted>)", objectId.toString())
    }

    @Test
    fun `default media names describe the supported fallback roles`() {
        assertEquals(
            listOf(DefaultMedia.USER_AVATAR, DefaultMedia.GROUP_AVATAR, DefaultMedia.GROUP_BANNER),
            DefaultMedia.entries,
        )
    }

    @Test
    fun `media content uses explicit reference semantics for its mutable buffer`() {
        val bytes = byteArrayOf(1, 2, 3)
        val content = MediaContent(bytes, "image/png")

        assertSame(bytes, content.bytes)
        assertNotEquals(content, MediaContent(bytes, "image/png"))
    }
}
