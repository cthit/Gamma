package it.chalmers.gamma.media

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.HexFormat
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalMediaStoreTest {
    @Test
    fun `stores verifies reads and deletes actual image files`() =
        run {
            val root = Files.createTempDirectory("gamma-media-test")
            try {
                val store = LocalMediaStore(root)
                val bytes =
                    ByteArrayOutputStream().use { output ->
                        ImageIO.write(BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output)
                        output.toByteArray()
                    }
                val uri = store.save(bytes, "image/png")
                assertEquals("png", uri.value.substringAfterLast('.'))
                assertContentEquals(bytes, store.read(uri, DefaultMedia.USER_AVATAR).bytes)
                assertFailsWith<InvalidMedia> { store.save(bytes, "image/jpeg") }
                assertFailsWith<InvalidMedia> { store.save(byteArrayOf(1, 2, 3), "image/png") }
                val legacyBoundaryImage = bytes.copyOf(MAX_MEDIA_BYTES)
                val legacyBoundaryUri = store.save(legacyBoundaryImage, "image/png")
                assertEquals(MAX_MEDIA_BYTES, store.read(legacyBoundaryUri, DefaultMedia.USER_AVATAR).bytes.size)
                assertFailsWith<MediaTooLarge> {
                    store.save(ByteArray(MAX_MEDIA_BYTES + 1), "image/png")
                }
                store.delete(legacyBoundaryUri)

                val nestedUri = MediaUri("123e4567-e89b-12d3-a456-426614174000/file.jpeg")
                Files.createDirectories(root.resolve("123e4567-e89b-12d3-a456-426614174000"))
                Files.write(root.resolve(nestedUri.value), bytes)
                assertContentEquals(bytes, store.read(nestedUri, DefaultMedia.USER_AVATAR).bytes)
                assertEquals("image/jpeg", store.read(nestedUri, DefaultMedia.USER_AVATAR).contentType)

                store.delete(uri)
                assertFailsWith<IllegalStateException> {
                    store.read(uri, DefaultMedia.USER_AVATAR)
                }
                assertFailsWith<IllegalArgumentException> { MediaUri("../../outside.png") }
                Unit
            } finally {
                Files.walk(root).use { paths ->
                    paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
                }
            }
        }

    @Test
    fun `default media roles load their exact packaged assets`() =
        run {
            val root = Files.createTempDirectory("gamma-default-media-test")
            try {
                val store = LocalMediaStore(root)
                val expectedHashes =
                    mapOf(
                        DefaultMedia.USER_AVATAR to
                            "2b91775b4e776285f235d466cb13a952cd78788691226f9158cd87b7a5f11882",
                        DefaultMedia.GROUP_AVATAR to
                            "94d9655b4943addbdbc4827281f22922be78874c4c74dba5cba8cb2057a45a0a",
                        DefaultMedia.GROUP_BANNER to
                            "d8951fdddbfce8445fd88238b965df73d99c7e0286cd111cbd257d9cef78845e",
                    )

                expectedHashes.forEach { (role, expectedHash) ->
                    val content = store.read(uri = null, fallback = role)
                    assertEquals("image/jpeg", content.contentType)
                    assertEquals(expectedHash, content.bytes.sha256())
                }
            } finally {
                Files.walk(root).use { paths ->
                    paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
                }
            }
        }

    @Test
    fun `reads and deletes files written by a legacy upload folder without a trailing separator`() =
        run {
            val sandbox = Files.createTempDirectory("gamma-legacy-media-prefix-test")
            try {
                val currentRoot = sandbox.resolve("current")
                val legacyPrefix = sandbox.resolve("uploads").toString()
                val store =
                    LocalMediaStore(
                        root = currentRoot,
                        legacyConcatenatedPrefix = legacyPrefix,
                    )
                val bytes =
                    ByteArrayOutputStream().use { output ->
                        ImageIO.write(BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output)
                        output.toByteArray()
                    }
                val uri = MediaUri("123e4567-e89b-12d3-a456-426614174000/file.png")
                val legacyFile = Path.of(legacyPrefix + uri.value)
                Files.createDirectories(checkNotNull(legacyFile.parent))
                Files.write(legacyFile, bytes)

                assertContentEquals(bytes, store.read(uri, DefaultMedia.USER_AVATAR).bytes)
                val newUri = store.save(bytes, "image/png")
                assertTrue(Files.isRegularFile(currentRoot.resolve(newUri.value)))

                store.delete(uri)
                assertFalse(Files.exists(legacyFile))
            } finally {
                Files.walk(sandbox).use { paths ->
                    paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
                }
            }
        }
}

private fun ByteArray.sha256(): String = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(this))
