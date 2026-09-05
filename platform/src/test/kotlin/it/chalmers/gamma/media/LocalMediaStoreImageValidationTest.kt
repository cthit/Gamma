package it.chalmers.gamma.media

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.zip.CRC32
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalMediaStoreImageValidationTest {
    @Test
    fun `fully decoded JPEG PNG and GIF images retain their canonical formats`() =
        withValidationStore { store ->
            val validImages =
                listOf(
                    ValidImage("jpeg", "image/jpeg", "jpg"),
                    ValidImage("png", "image/png", "png"),
                    ValidImage("gif", "image/gif", "gif"),
                )

            validImages.forEach { image ->
                val uri = store.save(encodedImage(image.writerFormat), image.contentType)
                assertEquals(image.extension, uri.value.substringAfterLast('.'))
            }
        }

    @Test
    fun `truncated JPEG PNG and GIF images are rejected`() =
        withValidationStore { store ->
            val truncatedImages =
                listOf(
                    encodedImage("jpeg").dropLastBytes(2),
                    encodedImage("png").dropLastBytes(12),
                    encodedImage("gif").dropLastBytes(1),
                )

            truncatedImages.forEach { bytes ->
                assertFailsWith<InvalidMedia> { store.save(bytes, null) }
            }
        }

    @Test
    fun `a PNG with corrupt chunk data is rejected`() =
        withValidationStore { store ->
            val corruptPng = encodedImage("png")
            corruptPng[PNG_IHDR_CRC_OFFSET] = (corruptPng[PNG_IHDR_CRC_OFFSET].toInt() xor 1).toByte()

            assertFailsWith<InvalidMedia> { store.save(corruptPng, "image/png") }
        }

    @Test
    fun `a JPEG decoder warning caused by truncated scan data is rejected`() =
        withValidationStore { store ->
            val jpeg = encodedImage("jpeg")
            val corruptJpeg = jpeg.copyOf(jpeg.size - JPEG_SCAN_BYTES_TO_REMOVE)
            corruptJpeg[corruptJpeg.lastIndex - 1] = 0xFF.toByte()
            corruptJpeg[corruptJpeg.lastIndex] = 0xD9.toByte()

            assertFailsWith<InvalidMedia> { store.save(corruptJpeg, "image/jpeg") }
        }

    @Test
    fun `trailing bytes after a complete GIF retain upload compatibility`() =
        withValidationStore { store ->
            val gifWithTrailingBytes = encodedImage("gif") + byteArrayOf(1, 2, 3)

            val uri = store.save(gifWithTrailingBytes, "image/gif")

            assertEquals("gif", uri.value.substringAfterLast('.'))
        }

    @Test
    fun `an image that would exhaust decoded pixel memory is rejected before decoding`() =
        withValidationStore { store ->
            val oversizedDimensions = encodedImage("png")
            oversizedDimensions.writeInt(PNG_WIDTH_OFFSET, 100_000)
            oversizedDimensions.writeInt(PNG_HEIGHT_OFFSET, 100_000)
            oversizedDimensions.updatePngHeaderCrc()

            assertFailsWith<InvalidMedia> { store.save(oversizedDimensions, "image/png") }
        }

    @Test
    fun `a compressed common phone photo is accepted near the pixel limit and a larger image is rejected`() =
        withValidationStore { store ->
            val commonPhonePhoto = compressedPng(width = 4_032, height = 3_024)
            assertTrue(commonPhonePhoto.size < TWO_MEBIBYTES)

            val uri = store.save(commonPhonePhoto, "image/png")
            assertEquals("png", uri.value.substringAfterLast('.'))

            val abovePixelLimit = commonPhonePhoto.copyOf()
            abovePixelLimit.writeInt(PNG_WIDTH_OFFSET, 4_300)
            abovePixelLimit.updatePngHeaderCrc()
            assertTrue(abovePixelLimit.size < TWO_MEBIBYTES)
            assertFailsWith<InvalidMedia> { store.save(abovePixelLimit, "image/png") }
        }

    @Test
    fun `image decode permits are shared across stores and recover after an interrupted waiter`() {
        val workers = Executors.newFixedThreadPool(CONCURRENT_SAVE_THREADS)
        val sandbox = Files.createTempDirectory("gamma-media-decode-permit-test")
        val heldPermits = CountDownLatch(2)
        val releasePermits = CountDownLatch(1)

        try {
            repeat(2) {
                workers.submit {
                    withProcessWideImageDecodePermit {
                        heldPermits.countDown()
                        releasePermits.await()
                    }
                }
            }
            assertTrue(heldPermits.await(5, TimeUnit.SECONDS))

            val firstStore = LocalMediaStore(sandbox.resolve("first"))
            val secondStore = LocalMediaStore(sandbox.resolve("second"))
            val image = encodedImage("png")
            val interruptedObjectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            val acceptedObjectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
            val recoveryObjectId = MediaObjectId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))

            val interruptedSave =
                workers.submit<MediaUri> {
                    firstStore.save(interruptedObjectId, image, "image/png")
                }
            val acceptedSave =
                workers.submit<MediaUri> {
                    secondStore.save(acceptedObjectId, image, "image/png")
                }
            assertFalse(interruptedSave.isDone)
            assertFalse(acceptedSave.isDone)
            assertTrue(interruptedSave.cancel(true))

            releasePermits.countDown()

            assertEquals("png", acceptedSave.get(5, TimeUnit.SECONDS).value.substringAfterLast('.'))
            assertEquals("png", firstStore.save(recoveryObjectId, image, "image/png").value.substringAfterLast('.'))
            assertFalse(Files.exists(sandbox.resolve("first/${interruptedObjectId.value}.png")))
        } finally {
            releasePermits.countDown()
            workers.shutdownNow()
            Files.walk(sandbox).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
            }
        }
    }
}

private data class ValidImage(
    val writerFormat: String,
    val contentType: String,
    val extension: String,
)

private fun encodedImage(format: String): ByteArray =
    ByteArrayOutputStream().use { output ->
        val image = BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB)
        image.setRGB(0, 0, 0x123456)
        check(ImageIO.write(image, format, output))
        output.toByteArray()
    }

private fun compressedPng(
    width: Int,
    height: Int,
): ByteArray =
    ByteArrayOutputStream().use { output ->
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        check(ImageIO.write(image, "png", output))
        image.flush()
        output.toByteArray()
    }

private fun ByteArray.dropLastBytes(count: Int): ByteArray = copyOf(size - count)

private fun ByteArray.writeInt(
    offset: Int,
    value: Int,
) {
    this[offset] = (value ushr 24).toByte()
    this[offset + 1] = (value ushr 16).toByte()
    this[offset + 2] = (value ushr 8).toByte()
    this[offset + 3] = value.toByte()
}

private fun ByteArray.updatePngHeaderCrc() {
    val crc = CRC32().apply { update(this@updatePngHeaderCrc, PNG_IHDR_TYPE_OFFSET, PNG_IHDR_TYPE_AND_DATA_BYTES) }
    writeInt(PNG_IHDR_CRC_OFFSET, crc.value.toInt())
}

private fun withValidationStore(block: (LocalMediaStore) -> Unit) =
    run {
        val root = Files.createTempDirectory("gamma-media-validation-test")
        try {
            block(LocalMediaStore(root))
        } finally {
            Files.walk(root).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
            }
        }
    }

private const val PNG_WIDTH_OFFSET = 16
private const val PNG_HEIGHT_OFFSET = 20
private const val PNG_IHDR_TYPE_OFFSET = 12
private const val PNG_IHDR_TYPE_AND_DATA_BYTES = 17
private const val PNG_IHDR_CRC_OFFSET = 29
private const val JPEG_SCAN_BYTES_TO_REMOVE = 20
private const val TWO_MEBIBYTES = 2 * 1024 * 1024
private const val CONCURRENT_SAVE_THREADS = 2
