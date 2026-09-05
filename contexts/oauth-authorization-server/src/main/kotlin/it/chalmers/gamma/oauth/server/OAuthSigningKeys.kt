package it.chalmers.gamma.oauth.server

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.Base64
import java.util.UUID

data class OAuthSigningKey(
    val keyId: String,
    val privateKey: RSAPrivateKey?,
    val publicKey: RSAPublicKey,
) {
    override fun toString(): String = "OAuthSigningKey(keyId=$keyId, privateKey=<redacted>, publicKey=<redacted>)"
}

data class OAuthSigningKeys(
    val current: OAuthSigningKey,
    val verificationKeys: List<OAuthSigningKey> = emptyList(),
) {
    init {
        requireNotNull(current.privateKey) { "The current OAuth signing key must include a private key" }
        require(verificationKeys.all { it.privateKey == null }) {
            "OAuth verification keys must not include private key material"
        }
        require((verificationKeys + current).map { it.keyId }.distinct().size == verificationKeys.size + 1) {
            "OAuth signing key ids must be unique"
        }
    }

    override fun toString(): String =
        "OAuthSigningKeys(current=${current.keyId}, verificationKeys=${verificationKeys.map { it.keyId }})"

    companion object {
        fun fromPkcs8Pem(pem: String): OAuthSigningKeys {
            require(pem.contains(PRIVATE_KEY_BEGIN) && pem.contains(PRIVATE_KEY_END)) {
                "The OAuth signing key must be a PEM-encoded PKCS#8 private key"
            }

            val encodedKey =
                pem
                    .substringAfter(PRIVATE_KEY_BEGIN)
                    .substringBefore(PRIVATE_KEY_END)
                    .filterNot(Char::isWhitespace)
            val privateKey =
                KeyFactory
                    .getInstance("RSA")
                    .generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKey)))
                    as? RSAPrivateCrtKey
                    ?: error("The OAuth signing key must include its RSA public exponent")
            val publicKey =
                KeyFactory
                    .getInstance("RSA")
                    .generatePublic(RSAPublicKeySpec(privateKey.modulus, privateKey.publicExponent)) as RSAPublicKey
            val keyId =
                Base64
                    .getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(MessageDigest.getInstance("SHA-256").digest(publicKey.encoded))

            return OAuthSigningKeys(OAuthSigningKey(keyId, privateKey, publicKey))
        }

        fun ephemeral(): OAuthSigningKeys {
            val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
            return OAuthSigningKeys(
                OAuthSigningKey(
                    UUID.randomUUID().toString(),
                    pair.private as RSAPrivateKey,
                    pair.public as RSAPublicKey,
                ),
            )
        }

        private const val PRIVATE_KEY_BEGIN = "-----BEGIN PRIVATE KEY-----"
        private const val PRIVATE_KEY_END = "-----END PRIVATE KEY-----"
    }
}
