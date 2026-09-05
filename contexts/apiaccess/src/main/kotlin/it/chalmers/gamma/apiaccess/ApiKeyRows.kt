package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as ApiTextsTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow

internal fun apiKeysWithDescription() =
    ApiKeysTable.join(
        ApiTextsTable,
        JoinType.LEFT,
        ApiKeysTable.descriptionId,
        ApiTextsTable.id,
    )

internal fun ResultRow.toApiKey() =
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
