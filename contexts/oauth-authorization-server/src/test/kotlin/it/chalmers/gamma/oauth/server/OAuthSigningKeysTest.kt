package it.chalmers.gamma.oauth.server

import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class OAuthSigningKeysTest {
    @Test
    fun `loads a PKCS8 RSA private key and derives its public key`() {
        val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()

        val loaded = OAuthSigningKeys.fromPkcs8Pem(pair.private.toPem()).current

        assertContentEquals((pair.private as RSAPrivateKey).encoded, loaded.privateKey?.encoded)
        assertContentEquals((pair.public as RSAPublicKey).encoded, loaded.publicKey.encoded)
    }

    @Test
    fun `derives a stable key id from the public key`() {
        val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val pem = pair.private.toPem()

        val first = OAuthSigningKeys.fromPkcs8Pem(pem).current
        val second = OAuthSigningKeys.fromPkcs8Pem(pem).current

        assertEquals(first.keyId, second.keyId)
    }
}

private fun java.security.PrivateKey.toPem(): String {
    val encodedPrivateKey = Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(encoded)
    return "-----BEGIN PRIVATE KEY-----\n$encodedPrivateKey\n-----END PRIVATE KEY-----"
}
