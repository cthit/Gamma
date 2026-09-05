package it.chalmers.gamma.media

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class LocalMediaStoreBehaviorTest {
    @Test
    fun `invalid uploads retain their domain errors`() =
        withMediaStore { _, store ->
            val emptyUpload = assertFailsWith<InvalidMedia> { store.save(byteArrayOf(), null) }
            assertEquals("Choose an image to upload.", emptyUpload.message)

            val oversizedUpload =
                assertFailsWith<MediaTooLarge> {
                    store.save(ByteArray(MAX_MEDIA_BYTES + 1), null)
                }
            assertEquals("Images must be no larger than 2 MiB.", oversizedUpload.message)

            val unsupportedUpload = assertFailsWith<InvalidMedia> { store.save(byteArrayOf(1, 2, 3), null) }
            assertEquals(
                "The uploaded file is not a valid JPEG, PNG, or GIF image.",
                unsupportedUpload.message,
            )

            val mismatchedType =
                assertFailsWith<InvalidMedia> {
                    store.save(pngBytes(rgb = 0x123456), "image/jpeg")
                }
            assertEquals("The image contents do not match its content type.", mismatchedType.message)
        }

    @Test
    fun `an explicit object id is idempotent for identical contents`() =
        withMediaStore { _, store ->
            val objectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            val bytes = pngBytes(rgb = 0x123456)

            val firstUri = store.save(objectId, bytes, "image/png")
            val retriedUri = store.save(objectId, bytes, "image/png")

            assertEquals(firstUri, retriedUri)
        }

    @Test
    fun `an explicit object id cannot replace different contents of the same format`() =
        withMediaStore { _, store ->
            val objectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            store.save(objectId, pngBytes(rgb = 0x123456), "image/png")

            val replacement =
                assertFailsWith<IllegalStateException> {
                    store.save(objectId, pngBytes(rgb = 0x654321), "image/png")
                }

            assertEquals("A media object cannot be replaced with different contents", replacement.message)
        }

    @Test
    fun `deleting an object id removes every supported extension`() =
        withMediaStore { root, store ->
            val objectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            val storedFiles =
                listOf("jpg", "png", "gif").map { extension ->
                    root.resolve("${objectId.value}.$extension").also { Files.write(it, byteArrayOf(1)) }
                }

            store.delete(objectId)

            storedFiles.forEach { storedFile -> assertFalse(Files.exists(storedFile)) }
        }
}

private fun pngBytes(rgb: Int): ByteArray =
    ByteArrayOutputStream().use { output ->
        val image = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        image.setRGB(0, 0, rgb)
        check(ImageIO.write(image, "png", output))
        output.toByteArray()
    }

private fun withMediaStore(block: (Path, LocalMediaStore) -> Unit) =
    run {
        val root = Files.createTempDirectory("gamma-media-behavior-test")
        try {
            block(root, LocalMediaStore(root))
        } finally {
            Files.walk(root).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
            }
        }
    }
