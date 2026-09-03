package it.chalmers.gamma.apiaccess

interface ApiTokenVerificationCache {
    fun match(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        presentedToken: RawApiToken,
    ): CachedApiTokenMatch

    fun remember(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    )

    data object Disabled : ApiTokenVerificationCache {
        override fun match(
            id: ApiKeyId,
            storedCredential: StoredApiCredential,
            presentedToken: RawApiToken,
        ) = CachedApiTokenMatch.MISS

        override fun remember(
            id: ApiKeyId,
            storedCredential: StoredApiCredential,
            token: RawApiToken,
        ) = Unit
    }
}

enum class CachedApiTokenMatch {
    MATCH,
    MISMATCH,
    MISS,
}

class ApiAccessNotFound(
    message: String,
) : RuntimeException(message)

class ApiAccessConflict(
    message: String,
) : RuntimeException(message)
