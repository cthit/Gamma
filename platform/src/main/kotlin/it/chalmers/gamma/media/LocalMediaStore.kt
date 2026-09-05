package it.chalmers.gamma.media

import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Semaphore
import java.util.zip.CRC32
import javax.imageio.ImageIO

/**
 * Stores media below an operator-controlled [root].
 *
 * [legacyConcatenatedPrefix] retains read and delete compatibility with paths persisted by older
 * Gamma releases. Both locations are trusted deployment configuration, not user input.
 */
class LocalMediaStore(
    private val root: Path,
    private val legacyConcatenatedPrefix: String? = null,
) : MediaStore {
    init {
        Files.createDirectories(root)
    }

    override fun save(
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri = save(MediaObjectId.generate(), bytes, declaredContentType)

    override fun save(
        objectId: MediaObjectId,
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri {
        if (bytes.isEmpty()) throw InvalidMedia("Choose an image to upload.")
        if (bytes.size > MAX_MEDIA_BYTES) throw MediaTooLarge("Images must be no larger than 2 MiB.")
        val detected =
            withProcessWideImageDecodePermit { detect(bytes) }
                ?: throw InvalidMedia("The uploaded file is not a valid JPEG, PNG, or GIF image.")
        val normalizedContentType = declaredContentType?.substringBefore(';')?.lowercase()
        if (normalizedContentType != null && normalizedContentType != detected.contentType) {
            throw InvalidMedia("The image contents do not match its content type.")
        }
        val uri = MediaUri("${objectId.value}.${detected.extension}")
        val target = safePath(uri)
        if (Files.isRegularFile(target)) {
            check(Files.readAllBytes(target).contentEquals(bytes)) {
                "A media object cannot be replaced with different contents"
            }
        } else {
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
        }
        return uri
    }

    override fun read(
        uri: MediaUri?,
        fallback: DefaultMedia,
    ): MediaContent {
        if (uri != null) {
            val currentFile = safePath(uri)
            val file =
                currentFile.takeIf(Files::isRegularFile)
                    ?: legacyPath(uri)?.takeIf(Files::isRegularFile)
            checkNotNull(file) { "Stored media file is missing" }
            return MediaContent(Files.readAllBytes(file), contentTypeFor(uri.value))
        }
        val defaultMedia = fallback.resource
        val bytes =
            LocalMediaStore::class.java.getResourceAsStream(defaultMedia.classpathPath)?.use { it.readBytes() }
                ?: error("Default media resource ${defaultMedia.classpathPath} is missing")
        return MediaContent(bytes, defaultMedia.contentType)
    }

    override fun delete(uri: MediaUri) {
        Files.deleteIfExists(safePath(uri))
        legacyPath(uri)?.let(Files::deleteIfExists)
    }

    override fun delete(objectId: MediaObjectId) {
        SUPPORTED_EXTENSIONS.forEach { extension ->
            val uri = MediaUri("${objectId.value}.$extension")
            Files.deleteIfExists(safePath(uri))
            legacyPath(uri)?.let(Files::deleteIfExists)
        }
    }

    private fun safePath(uri: MediaUri): Path {
        val normalizedRoot = root.toAbsolutePath().normalize()
        val resolved = normalizedRoot.resolve(uri.value).normalize()
        val relative = normalizedRoot.relativize(resolved)
        check(resolved.startsWith(normalizedRoot) && relative.nameCount in 1..2) {
            "Media path escaped its storage directory"
        }
        return resolved
    }

    private fun legacyPath(uri: MediaUri): Path? {
        val prefix = legacyConcatenatedPrefix ?: return null
        val absolutePrefix = Path.of(prefix).toAbsolutePath().normalize()
        val resolved = Path.of(prefix + uri.value).toAbsolutePath().normalize()
        val containingDirectory = absolutePrefix.parent ?: return null
        check(resolved.startsWith(containingDirectory)) { "Legacy media path escaped its containing directory" }
        return resolved
    }

    private fun detect(bytes: ByteArray): DetectedImage? =
        try {
            ImageIO.createImageInputStream(ByteArrayInputStream(bytes)).use { input ->
                val reader = ImageIO.getImageReaders(input).asSequence().firstOrNull() ?: return null
                try {
                    val detected =
                        when (reader.formatName.lowercase()) {
                            "jpeg", "jpg" -> DetectedImage("jpg", "image/jpeg")
                            "png" -> DetectedImage("png", "image/png")
                            "gif" -> DetectedImage("gif", "image/gif")
                            else -> return null
                        }
                    if (!hasCompleteImageStructure(bytes, detected)) return null

                    var decoderReportedWarning = false
                    // A warning means the decoder recovered from malformed input. Warning text is
                    // plug-in and locale dependent, so classifying messages would be unreliable.
                    reader.addIIOReadWarningListener { _, _ -> decoderReportedWarning = true }
                    reader.setInput(input, false, false)

                    val frameCount = reader.getNumImages(true)
                    if (frameCount !in 1..MAX_DECODED_FRAMES) return null

                    var totalDecodedPixels = 0L
                    repeat(frameCount) { frameIndex ->
                        val width = reader.getWidth(frameIndex)
                        val height = reader.getHeight(frameIndex)
                        val decodedPixels = width.toLong() * height.toLong()
                        if (width <= 0 || height <= 0 || decodedPixels > MAX_DECODED_PIXELS_PER_FRAME) return null

                        totalDecodedPixels += decodedPixels
                        if (totalDecodedPixels > MAX_TOTAL_DECODED_PIXELS) return null

                        reader.read(frameIndex).flush()
                    }

                    detected.takeUnless { decoderReportedWarning }
                } finally {
                    reader.dispose()
                }
            }
        } catch (_: IOException) {
            null
        } catch (_: RuntimeException) {
            // ImageIO plug-ins may surface malformed dimensions and metadata as runtime failures.
            null
        }

    private fun hasCompleteImageStructure(
        bytes: ByteArray,
        detected: DetectedImage,
    ): Boolean =
        when (detected.extension) {
            "jpg" -> bytes.hasJpegEndOfImage()
            "png" -> bytes.hasValidPngChunks()
            "gif" -> bytes.hasCompleteGifDataStream()
            else -> false
        }

    private fun contentTypeFor(value: String): String =
        when (value.substringAfterLast('.')) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            else -> error("Unsupported stored image type")
        }

    private data class DetectedImage(
        val extension: String,
        val contentType: String,
    )

    private companion object {
        const val MAX_DECODED_FRAMES = 256

        // Gamma displays images at modest sizes. This still accepts common 4032x3024 phone photos.
        const val MAX_DECODED_PIXELS_PER_FRAME = 13_000_000L
        const val MAX_TOTAL_DECODED_PIXELS = 25_000_000L
        val SUPPORTED_EXTENSIONS = listOf("jpg", "png", "gif")
    }
}

// Image decoding is memory-heavy, so this gate is shared by every store in the process.
private val processWideImageDecodePermits = Semaphore(MAX_CONCURRENT_IMAGE_DECODES, true)

internal fun <T> withProcessWideImageDecodePermit(decode: () -> T): T {
    processWideImageDecodePermits.acquire()
    return try {
        decode()
    } finally {
        processWideImageDecodePermits.release()
    }
}

private const val MAX_CONCURRENT_IMAGE_DECODES = 2

private fun ByteArray.hasJpegEndOfImage(): Boolean {
    for (index in 1 until size) {
        if (this[index - 1] == 0xFF.toByte() && this[index] == 0xD9.toByte()) return true
    }
    return false
}

private fun ByteArray.hasValidPngChunks(): Boolean {
    if (size < PNG_SIGNATURE.size || !copyOfRange(0, PNG_SIGNATURE.size).contentEquals(PNG_SIGNATURE)) return false

    var chunkOffset = PNG_SIGNATURE.size
    while (chunkOffset <= size - PNG_CHUNK_OVERHEAD_BYTES) {
        val dataLength = unsignedIntAt(chunkOffset)
        val bytesAfterLength = size - chunkOffset - PNG_LENGTH_BYTES
        if (dataLength > bytesAfterLength - PNG_TYPE_AND_CRC_BYTES) return false

        val typeOffset = chunkOffset + PNG_LENGTH_BYTES
        val dataOffset = typeOffset + PNG_TYPE_BYTES
        val crcOffset = dataOffset + dataLength.toInt()
        val calculatedCrc =
            CRC32().apply {
                update(
                    this@hasValidPngChunks,
                    typeOffset,
                    PNG_TYPE_BYTES + dataLength.toInt(),
                )
            }
        if (calculatedCrc.value != unsignedIntAt(crcOffset)) return false

        val isImageEnd =
            this[typeOffset] == 'I'.code.toByte() &&
                this[typeOffset + 1] == 'E'.code.toByte() &&
                this[typeOffset + 2] == 'N'.code.toByte() &&
                this[typeOffset + 3] == 'D'.code.toByte()
        if (isImageEnd) return dataLength == 0L

        chunkOffset = crcOffset + PNG_CRC_BYTES
    }
    return false
}

private fun ByteArray.hasCompleteGifDataStream(): Boolean {
    if (size < GIF_LOGICAL_SCREEN_END_OFFSET) return false

    var blockOffset = GIF_LOGICAL_SCREEN_END_OFFSET
    val logicalScreenFlags = this[GIF_LOGICAL_SCREEN_FLAGS_OFFSET].toInt() and 0xFF
    if (logicalScreenFlags and GIF_COLOR_TABLE_FLAG != 0) {
        blockOffset += gifColorTableBytes(logicalScreenFlags)
    }

    while (blockOffset < size) {
        when (this[blockOffset].toInt() and 0xFF) {
            GIF_TRAILER -> {
                return true
            }

            GIF_EXTENSION -> {
                blockOffset = gifSubBlocksEnd(blockOffset + GIF_EXTENSION_HEADER_BYTES)
            }

            GIF_IMAGE_DESCRIPTOR -> {
                if (blockOffset > size - GIF_IMAGE_DESCRIPTOR_BYTES) return false
                val imageFlags = this[blockOffset + GIF_IMAGE_FLAGS_OFFSET].toInt() and 0xFF
                blockOffset += GIF_IMAGE_DESCRIPTOR_BYTES
                if (imageFlags and GIF_COLOR_TABLE_FLAG != 0) {
                    blockOffset += gifColorTableBytes(imageFlags)
                }
                if (blockOffset >= size) return false
                blockOffset = gifSubBlocksEnd(blockOffset + GIF_LZW_CODE_SIZE_BYTES)
            }

            else -> {
                return false
            }
        }
        if (blockOffset < 0) return false
    }
    return false
}

private fun ByteArray.gifSubBlocksEnd(firstBlockSizeOffset: Int): Int {
    var blockSizeOffset = firstBlockSizeOffset
    while (blockSizeOffset < size) {
        val blockSize = this[blockSizeOffset].toInt() and 0xFF
        if (blockSize == 0) return blockSizeOffset + 1
        if (blockSizeOffset > size - blockSize - 1) return -1
        blockSizeOffset += blockSize + 1
    }
    return -1
}

private fun gifColorTableBytes(flags: Int): Int = 3 * (1 shl ((flags and GIF_COLOR_TABLE_SIZE_BITS) + 1))

private fun ByteArray.unsignedIntAt(offset: Int): Long =
    ((this[offset].toLong() and 0xFF) shl 24) or
        ((this[offset + 1].toLong() and 0xFF) shl 16) or
        ((this[offset + 2].toLong() and 0xFF) shl 8) or
        (this[offset + 3].toLong() and 0xFF)

private val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
private const val PNG_LENGTH_BYTES = 4
private const val PNG_TYPE_BYTES = 4
private const val PNG_CRC_BYTES = 4
private const val PNG_TYPE_AND_CRC_BYTES = PNG_TYPE_BYTES + PNG_CRC_BYTES
private const val PNG_CHUNK_OVERHEAD_BYTES = PNG_LENGTH_BYTES + PNG_TYPE_AND_CRC_BYTES
private const val GIF_LOGICAL_SCREEN_FLAGS_OFFSET = 10
private const val GIF_LOGICAL_SCREEN_END_OFFSET = 13
private const val GIF_COLOR_TABLE_FLAG = 0x80
private const val GIF_COLOR_TABLE_SIZE_BITS = 0x07
private const val GIF_EXTENSION = 0x21
private const val GIF_IMAGE_DESCRIPTOR = 0x2C
private const val GIF_TRAILER = 0x3B
private const val GIF_EXTENSION_HEADER_BYTES = 2
private const val GIF_IMAGE_DESCRIPTOR_BYTES = 10
private const val GIF_IMAGE_FLAGS_OFFSET = 9
private const val GIF_LZW_CODE_SIZE_BYTES = 1

private val DefaultMedia.resource: DefaultMediaResource
    get() =
        when (this) {
            DefaultMedia.USER_AVATAR -> {
                DefaultMediaResource(
                    "/it/chalmers/gamma/media/defaults/default_user_avatar.jpg",
                    "image/jpeg",
                )
            }

            DefaultMedia.GROUP_AVATAR -> {
                DefaultMediaResource(
                    "/it/chalmers/gamma/media/defaults/default_group_avatar.jpg",
                    "image/jpeg",
                )
            }

            DefaultMedia.GROUP_BANNER -> {
                DefaultMediaResource(
                    "/it/chalmers/gamma/media/defaults/default_group_banner.jpg",
                    "image/jpeg",
                )
            }
        }

private data class DefaultMediaResource(
    val classpathPath: String,
    val contentType: String,
)
