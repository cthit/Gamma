package it.chalmers.gamma.users

import it.chalmers.gamma.users.Cid
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class UserTokenTypesTest {
    @Test
    fun `activation cids use the user cid boundaries`() {
        assertEquals("abcd", Cid("abcd").value)
        assertEquals(Cid("abcdefghijkl"), Cid("abcdefghijkl"))
    }

    @Test
    fun `onboarding diagnostics redact the allowed cid`() {
        val cid = Cid("privatecid")
        val rendered = listOf(cid, PendingActivation(cid, Instant.EPOCH)).joinToString()

        assertFalse(rendered.contains(cid.value))
    }

    @Test
    fun `onboarding cids reject case digits punctuation and invalid lengths`() {
        listOf("abc", "abcdefghijklm", "new1", "Newcid", "new.cid").forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { Cid(value) }
        }
    }

    @Test
    fun `registration tokens enforce bounds and redact diagnostics`() {
        val token = RegistrationToken("r".repeat(32))

        assertNotEquals(token.value, token.toString())
        assertEquals(100, RegistrationToken("r".repeat(100)).value.length)
        assertFailsWith<IllegalArgumentException> { RegistrationToken("r".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { RegistrationToken("r".repeat(101)) }
    }

    @Test
    fun `password reset tokens enforce bounds and redact diagnostics`() {
        val token = PasswordResetToken("p".repeat(32))

        assertNotEquals(token.value, token.toString())
        assertEquals(100, PasswordResetToken("p".repeat(100)).value.length)
        assertFailsWith<IllegalArgumentException> { PasswordResetToken("p".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { PasswordResetToken("p".repeat(101)) }
    }
}
