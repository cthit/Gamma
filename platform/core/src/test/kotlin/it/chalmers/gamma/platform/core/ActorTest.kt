package it.chalmers.gamma.platform.core

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ActorTest {
    @Test
    fun `actors preserve only trusted principal identifiers`() {
        val userId = UUID.randomUUID()
        val credentialId = UUID.randomUUID()

        assertEquals(userId, Actor.User(ActorUserId(userId)).userId.value)
        assertEquals(credentialId, Actor.ApiClient(ApiClientCredentialId(credentialId)).credentialId.value)
        assertFalse(Actor.User(ActorUserId(userId)).toString().contains(userId.toString()))
        assertFalse(Actor.ApiClient(ApiClientCredentialId(credentialId)).toString().contains(credentialId.toString()))
        assertFalse(ActorUserId(userId).toString().contains(userId.toString()))
        assertFalse(ApiClientCredentialId(credentialId).toString().contains(credentialId.toString()))
    }
}
