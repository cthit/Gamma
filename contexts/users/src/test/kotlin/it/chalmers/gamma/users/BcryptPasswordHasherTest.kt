package it.chalmers.gamma.users

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BcryptPasswordHasherTest {
    private val hasher = BcryptPasswordHasher(cost = 10)

    @Test
    fun `hashes with a unique salt and verifies without exposing secrets`() {
        val password = PlainTextPassword("correct horse battery staple")
        val first = hasher.hash(password)
        val second = hasher.hash(password)

        assertNotEquals(first, second)
        assertTrue(first.value.startsWith("{bcrypt}$2"))
        assertTrue(hasher.verify(password, first))
        assertFalse(hasher.verify(PlainTextPassword("incorrect horse battery staple"), first))
        assertNotEquals(first.value, first.toString())
        assertNotEquals(password.value, password.toString())
    }

    @Test
    fun `verifies the spring-compatible regression hash`() {
        val hash =
            PasswordHash(
                "{bcrypt}${'$'}2y${'$'}10${'$'}cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS",
            )

        assertTrue(hasher.verify(PlainTextPassword("password1337"), hash))
    }
}
