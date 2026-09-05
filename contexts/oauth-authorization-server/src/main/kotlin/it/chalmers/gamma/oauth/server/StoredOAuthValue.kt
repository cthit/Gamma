package it.chalmers.gamma.oauth.server

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.Instant
import java.util.Date

@Serializable
internal data class StoredOAuthValue(
    val kind: StoredOAuthValueKind,
    val text: String? = null,
    val integer: Long? = null,
    val decimal: String? = null,
    val boolean: Boolean? = null,
    val values: List<StoredOAuthValue> = emptyList(),
    val entries: Map<String, StoredOAuthValue> = emptyMap(),
) {
    fun validate(depth: Int = 0) {
        require(depth <= MAXIMUM_VALUE_DEPTH) { "OAuth value nesting is too deep" }
        require(text == null || text.length <= MAXIMUM_VALUE_STRING_LENGTH) { "OAuth string value is too large" }
        require(values.size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth value list is too large" }
        require(entries.size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth value object is too large" }
        values.forEach { it.validate(depth + 1) }
        entries.forEach { (key, value) ->
            require(key.length in 1..MAXIMUM_VALUE_STRING_LENGTH) { "OAuth object key is invalid" }
            value.validate(depth + 1)
        }
    }

    override fun toString(): String = "StoredOAuthValue(kind=$kind, value=<redacted>)"
}

@Serializable
internal enum class StoredOAuthValueKind {
    NULL,
    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    INSTANT,
    DATE,
    LIST,
    SET,
    OBJECT,
}

internal fun Map<String, *>.toStoredValues(): Map<String, StoredOAuthValue> {
    require(size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth value map is too large" }
    return entries.associate { (key, value) ->
        require(key.length in 1..MAXIMUM_VALUE_STRING_LENGTH) { "OAuth value key is invalid" }
        key to value.toStoredValue(0)
    }
}

private fun Any?.toStoredValue(depth: Int): StoredOAuthValue {
    require(depth <= MAXIMUM_VALUE_DEPTH) { "OAuth value nesting is too deep" }
    return when (this) {
        null -> {
            StoredOAuthValue(StoredOAuthValueKind.NULL)
        }

        is String -> {
            require(length <= MAXIMUM_VALUE_STRING_LENGTH) { "OAuth string value is too large" }
            StoredOAuthValue(StoredOAuthValueKind.STRING, text = this)
        }

        is Char -> {
            StoredOAuthValue(StoredOAuthValueKind.STRING, text = toString())
        }

        is Boolean -> {
            StoredOAuthValue(StoredOAuthValueKind.BOOLEAN, boolean = this)
        }

        is Byte,
        is Short,
        is Int,
        is Long,
        -> {
            StoredOAuthValue(StoredOAuthValueKind.INTEGER, integer = (this as Number).toLong())
        }

        is Float,
        is Double,
        is BigDecimal,
        is BigInteger,
        -> {
            val encoded = toString()
            require(encoded.length <= 256) { "OAuth numeric value is too large" }
            StoredOAuthValue(StoredOAuthValueKind.DECIMAL, decimal = encoded)
        }

        is Instant -> {
            StoredOAuthValue(StoredOAuthValueKind.INSTANT, text = toString())
        }

        is Date -> {
            StoredOAuthValue(StoredOAuthValueKind.DATE, integer = time)
        }

        is URI,
        is URL,
        -> {
            StoredOAuthValue(StoredOAuthValueKind.STRING, text = toString())
        }

        is Set<*> -> {
            require(size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth set value is too large" }
            StoredOAuthValue(StoredOAuthValueKind.SET, values = map { it.toStoredValue(depth + 1) })
        }

        is Iterable<*> -> {
            val items = take(MAXIMUM_VALUE_COLLECTION_SIZE + 1).toList()
            require(items.size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth list value is too large" }
            StoredOAuthValue(StoredOAuthValueKind.LIST, values = items.map { it.toStoredValue(depth + 1) })
        }

        is Array<*> -> {
            require(size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth array value is too large" }
            StoredOAuthValue(StoredOAuthValueKind.LIST, values = map { it.toStoredValue(depth + 1) })
        }

        is Map<*, *> -> {
            require(size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth object value is too large" }
            val mapped =
                entries.associate { (key, value) ->
                    require(key is String && key.length in 1..MAXIMUM_VALUE_STRING_LENGTH) {
                        "OAuth object key is invalid"
                    }
                    key to value.toStoredValue(depth + 1)
                }
            StoredOAuthValue(StoredOAuthValueKind.OBJECT, entries = mapped)
        }

        else -> {
            val typeName = this::class.qualifiedName ?: "null"
            throw IllegalArgumentException(
                "OAuth value type is unsupported: $typeName",
            )
        }
    }
}

private fun StoredOAuthValue.toRuntimeValue(): Any? =
    when (kind) {
        StoredOAuthValueKind.NULL -> null
        StoredOAuthValueKind.STRING -> requireNotNull(text)
        StoredOAuthValueKind.INTEGER -> requireNotNull(integer)
        StoredOAuthValueKind.DECIMAL -> requireNotNull(decimal).toBigDecimal()
        StoredOAuthValueKind.BOOLEAN -> requireNotNull(boolean)
        StoredOAuthValueKind.INSTANT -> Instant.parse(requireNotNull(text))
        StoredOAuthValueKind.DATE -> Date(requireNotNull(integer))
        StoredOAuthValueKind.LIST -> values.map { it.toRuntimeValue() }
        StoredOAuthValueKind.SET -> values.mapTo(linkedSetOf()) { it.toRuntimeValue() }
        StoredOAuthValueKind.OBJECT -> entries.mapValues { it.value.toRuntimeValue() }
    }

internal fun Map<String, StoredOAuthValue>.toRuntimeValues(): Map<String, Any> =
    entries.mapNotNull { (key, value) -> value.toRuntimeValue()?.let { key to it } }.toMap()

internal const val MAXIMUM_SENSITIVE_VALUE_LENGTH = 16_384
internal const val MAXIMUM_VALUE_STRING_LENGTH = 16_384
internal const val MAXIMUM_VALUE_COLLECTION_SIZE = 256
private const val MAXIMUM_VALUE_DEPTH = 8
