package it.chalmers.gamma.apiaccess

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as ApiTextsTable
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.UUID

class ApiKeyStore(
    private val database: DatabaseFactory,
    private val verificationCache: ApiTokenVerificationCache = ApiTokenVerificationCache.Disabled,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    fun findApiKey(id: ApiKeyId): ApiKey? =
        database.transaction(readOnly = true) {
            apiKeysWithDescription()
                .selectAll()
                .where { ApiKeysTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?.toApiKey()
        }

    fun listApiKeys(): List<ApiKey> =
        database.transaction(readOnly = true) {
            apiKeysWithDescription()
                .selectAll()
                .orderBy(ApiKeysTable.name, SortOrder.ASC)
                .map { it.toApiKey() }
        }

    fun authenticate(
        type: ApiKeyType,
        id: ApiKeyId,
        token: RawApiToken,
    ): ApiKey? {
        val storedCredential =
            database.transaction(readOnly = true) {
                ApiKeysTable
                    .selectAll()
                    .where { (ApiKeysTable.id eq id.value) and (ApiKeysTable.type eq type.name) }
                    .limit(1)
                    .firstOrNull()
                    ?.get(ApiKeysTable.token)
                    ?.let(::StoredApiCredential)
            } ?: return null
        when (cachedMatch(id, storedCredential, token)) {
            CachedApiTokenMatch.MATCH -> {
                return findApiKey(id)
            }

            CachedApiTokenMatch.MISMATCH -> {
                return null
            }

            CachedApiTokenMatch.MISS -> {
                if (!credentialMatches(token, storedCredential)) {
                    return null
                }
                rememberVerifiedToken(id, storedCredential, token)
            }
        }
        return findApiKey(id)
    }

    fun infoSettings(id: ApiKeyId): ApiKeyInfoSettings? =
        storedSettings(id, ApiKeyType.INFO)?.let { settings ->
            ApiKeyInfoSettings(
                version = settings.version,
                superGroupTypes = settings.superGroupTypes.map { it.type },
            )
        }

    fun accountScaffoldSettings(id: ApiKeyId): ApiKeyAccountScaffoldSettings? =
        storedSettings(id, ApiKeyType.ACCOUNT_SCAFFOLD)?.let { settings ->
            ApiKeyAccountScaffoldSettings(settings.version, settings.superGroupTypes)
        }

    @Suppress("TooGenericExceptionThrown") // Preserve the legacy adapter's wrong-key-type contract.
    private fun storedSettings(
        id: ApiKeyId,
        expectedType: ApiKeyType,
    ): StoredApiKeySettings? =
        database.transaction(readOnly = true) {
            val apiKey =
                ApiKeysTable
                    .selectAll()
                    .where { ApiKeysTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?: return@transaction null
            val row = loadUniqueApiKeySettingsRow(id)
            val actualType = apiKey[ApiKeysTable.type]
            if (actualType != expectedType.name) throw RuntimeException("Unexpected api key type")
            if (row == null) return@transaction null
            val settingsId = row[ApiKeySettingsTable.id]
            val managed =
                ManagedApiKeyTypesTable
                    .selectAll()
                    .where { ManagedApiKeyTypesTable.settingsId eq settingsId }
                    .map { it[ManagedApiKeyTypesTable.type] }
                    .toSet()
            val types =
                ApiKeyTypesTable
                    .selectAll()
                    .where { ApiKeyTypesTable.settingsId eq settingsId }
                    .orderBy(ApiKeyTypesTable.type, SortOrder.ASC)
                    .map {
                        val type = it[ApiKeyTypesTable.type]
                        SuperGroupTypeSetting(SuperGroupType(type), type in managed)
                    }
            StoredApiKeySettings(row[ApiKeySettingsTable.version] ?: 0, types)
        }

    fun createApiKey(
        name: ApiKeyName,
        description: LocalizedText,
        type: ApiKeyType,
    ): CreatedApiKey {
        val id = ApiKeyId.generate()
        val credential = generateCredential()
        val descriptionId = UUID.randomUUID()
        val now = now()
        val apiKey = ApiKey(id, name, description, type, version = 0)
        database.transaction {
            ApiTextsTable.insert {
                it[ApiTextsTable.id] = descriptionId
                it[sv] = description.sv.value
                it[en] = description.en.value
                it[createdAt] = now
            }
            ApiKeysTable.insert {
                it[ApiKeysTable.id] = id.value
                it[ApiKeysTable.name] = name.value
                it[token] = credential.stored.value
                it[ApiKeysTable.type] = type.name
                it[createdAt] = now
                it[updatedAt] = now
                it[version] = 0
                it[ApiKeysTable.descriptionId] = descriptionId
            }
            if (type == ApiKeyType.INFO || type == ApiKeyType.ACCOUNT_SCAFFOLD) {
                ApiKeySettingsTable.insert {
                    it[ApiKeySettingsTable.id] = UUID.randomUUID()
                    it[createdAt] = now
                    it[updatedAt] = now
                    it[version] = 0
                    it[apiKeyId] = id.value
                }
            }
        }
        rememberVerifiedToken(id, credential.stored, credential.raw)
        return CreatedApiKey(apiKey, credential.raw)
    }

    fun resetToken(id: ApiKeyId): RawApiToken {
        val credential = generateCredential()
        database.transaction {
            val apiKey =
                ApiKeysTable
                    .selectAll()
                    .where { ApiKeysTable.id eq id.value }
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?: throw ApiAccessNotFound("API key does not exist")
            val currentVersion = apiKey[ApiKeysTable.version] ?: 0
            val changed =
                ApiKeysTable.update({ ApiKeysTable.id eq id.value }) {
                    it[ApiKeysTable.token] = credential.stored.value
                    it[updatedAt] = now()
                    it[version] = currentVersion + 1
                }
            if (changed != 1) throw ApiAccessNotFound("API key does not exist")
        }
        rememberVerifiedToken(id, credential.stored, credential.raw)
        return credential.raw
    }

    fun updateInfoSettings(
        id: ApiKeyId,
        settings: ApiKeyInfoSettings,
    ) = updateSettings(
        id,
        ApiKeyType.INFO,
        settings.version,
        settings.superGroupTypes.map { SuperGroupTypeSetting(it, requiresManaged = false) },
    )

    fun updateAccountScaffoldSettings(
        id: ApiKeyId,
        settings: ApiKeyAccountScaffoldSettings,
    ) = updateSettings(id, ApiKeyType.ACCOUNT_SCAFFOLD, settings.version, settings.superGroupTypes)

    @Suppress("TooGenericExceptionThrown") // Preserve the legacy adapter's wrong-key-type contract.
    private fun updateSettings(
        id: ApiKeyId,
        expectedType: ApiKeyType,
        version: Int,
        superGroupTypes: List<SuperGroupTypeSetting>,
    ) {
        database.transaction {
            val apiKey =
                ApiKeysTable
                    .selectAll()
                    .where { ApiKeysTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?: throw ApiAccessConflict("Settings are missing or have been changed")
            val row = loadUniqueApiKeySettingsRow(id)
            val actualType = apiKey[ApiKeysTable.type]
            if (actualType != expectedType.name) throw RuntimeException("Unexpected api key type")
            if (row == null || (row[ApiKeySettingsTable.version] ?: 0) != version) {
                throw ApiAccessConflict("Settings are missing or have been changed")
            }
            val settingsId = row[ApiKeySettingsTable.id]
            ApiKeyTypesTable.deleteWhere { ApiKeyTypesTable.settingsId eq settingsId }
            ManagedApiKeyTypesTable.deleteWhere { ManagedApiKeyTypesTable.settingsId eq settingsId }
            superGroupTypes.distinctBy { it.type }.forEach { setting ->
                ApiKeyTypesTable.insert {
                    it[ApiKeyTypesTable.settingsId] = settingsId
                    it[createdAt] = now()
                    it[type] = setting.type.value
                }
                if (setting.requiresManaged) {
                    ManagedApiKeyTypesTable.insert {
                        it[ManagedApiKeyTypesTable.settingsId] = settingsId
                        it[createdAt] = now()
                        it[type] = setting.type.value
                    }
                }
            }
            val changed =
                ApiKeySettingsTable.update(
                    where = {
                        (ApiKeySettingsTable.id eq settingsId) and
                            ApiKeySettingsTable.version.matchesStoredVersion(version)
                    },
                ) {
                    it[ApiKeySettingsTable.version] = version + 1
                    it[updatedAt] = now()
                }
            if (changed != 1) throw ApiAccessConflict("Settings are missing or have been changed")
        }
    }

    fun deleteApiKey(id: ApiKeyId) {
        database.transaction {
            if (!deleteApiKeyRow(id)) throw ApiAccessNotFound("API key does not exist")
        }
    }

    fun deleteOwnedBy(ownedApiKeyIds: Set<ApiKeyId>) {
        database.transaction {
            ownedApiKeyIds.forEach { deleteApiKeyRow(it) }
        }
    }

    private fun JdbcTransaction.deleteApiKeyRow(id: ApiKeyId): Boolean {
        val row =
            ApiKeysTable
                .selectAll()
                .where { ApiKeysTable.id eq id.value }
                .forUpdate()
                .limit(1)
                .firstOrNull()
                ?: return false
        val descriptionId = row[ApiKeysTable.descriptionId]
        val settingsId = loadUniqueApiKeySettingsRow(id)?.get(ApiKeySettingsTable.id)
        if (settingsId != null) {
            ManagedApiKeyTypesTable.deleteWhere { ManagedApiKeyTypesTable.settingsId eq settingsId }
            ApiKeyTypesTable.deleteWhere { ApiKeyTypesTable.settingsId eq settingsId }
            ApiKeySettingsTable.deleteWhere { ApiKeySettingsTable.id eq settingsId }
        }
        if (ApiKeysTable.deleteWhere { ApiKeysTable.id eq id.value } != 1) return false
        if (descriptionId != null) ApiTextsTable.deleteWhere { ApiTextsTable.id eq descriptionId }
        return true
    }

    private fun apiKeysWithDescription() =
        ApiKeysTable.join(
            ApiTextsTable,
            JoinType.LEFT,
            ApiKeysTable.descriptionId,
            ApiTextsTable.id,
        )

    private fun ResultRow.toApiKey() =
        ApiKey(
            id = ApiKeyId(this[ApiKeysTable.id]),
            name = ApiKeyName(this[ApiKeysTable.name]),
            description =
                LocalizedText.of(
                    getOrNull(ApiTextsTable.sv).orEmpty(),
                    getOrNull(ApiTextsTable.en).orEmpty(),
                ),
            type = ApiKeyType.valueOf(this[ApiKeysTable.type]),
            version = this[ApiKeysTable.version] ?: 0,
        )

    private fun generateCredential(): GeneratedApiCredential {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val raw = RawApiToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        return GeneratedApiCredential(raw, hash(raw))
    }

    private fun credentialMatches(
        token: RawApiToken,
        storedCredential: StoredApiCredential,
    ): Boolean =
        BCrypt
            .verifyer()
            .verify(
                token.value.toCharArray(),
                storedCredential.value.removePrefix("{bcrypt}").toCharArray(),
            ).verified

    private fun hash(token: RawApiToken): StoredApiCredential =
        StoredApiCredential("{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, token.value.toCharArray()))

    @Suppress("TooGenericExceptionCaught") // Redis is an optional accelerator.
    private fun cachedMatch(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ): CachedApiTokenMatch =
        try {
            verificationCache.match(id, storedCredential, token)
        } catch (_: Exception) {
            CachedApiTokenMatch.MISS
        }

    @Suppress("TooGenericExceptionCaught") // A cache outage must not hide a successful durable operation.
    private fun rememberVerifiedToken(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ) {
        try {
            verificationCache.remember(id, storedCredential, token)
        } catch (_: Exception) {
            // The database credential remains authoritative.
        }
    }

    private fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}

private data class StoredApiKeySettings(
    val version: Int,
    val superGroupTypes: List<SuperGroupTypeSetting>,
)

private data class GeneratedApiCredential(
    val raw: RawApiToken,
    val stored: StoredApiCredential,
)

private fun JdbcTransaction.loadUniqueApiKeySettingsRow(id: ApiKeyId): ResultRow? {
    val rows =
        ApiKeySettingsTable
            .selectAll()
            .where { ApiKeySettingsTable.apiKeyId eq id.value }
            .limit(2)
            .toList()
    check(rows.size <= 1) { "Multiple API key settings rows exist for one API key" }
    return rows.firstOrNull()
}
