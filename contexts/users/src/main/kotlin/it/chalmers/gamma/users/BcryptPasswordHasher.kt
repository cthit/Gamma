package it.chalmers.gamma.users

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.concurrent.Semaphore

class BcryptPasswordHasher(
    private val cost: Int = 12,
    maximumConcurrentOperations: Int = 8,
) : PasswordHasher {
    private val permits = Semaphore(maximumConcurrentOperations, true)
    private val dummyHash: PasswordHash

    init {
        require(cost in 10..16) { "bcrypt cost must be between 10 and 16" }
        require(maximumConcurrentOperations > 0) { "Maximum concurrent bcrypt operations must be positive" }
        dummyHash = hash(PlainTextPassword("dummy password used only to equalize verification work"))
    }

    override fun hash(password: PlainTextPassword): PasswordHash =
        withPermit {
            PasswordHash(
                "{bcrypt}" + BCrypt.withDefaults().hashToString(cost, password.value.toCharArray()),
            )
        }

    override fun verify(
        password: PlainTextPassword,
        hash: PasswordHash,
    ): Boolean =
        withPermit {
            BCrypt
                .verifyer()
                .verify(
                    password.value.toCharArray(),
                    hash.value.removePrefix("{bcrypt}").toCharArray(),
                ).verified
        }

    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean = verify(password, dummyHash)

    private fun <T> withPermit(operation: () -> T): T {
        permits.acquire()
        return try {
            operation()
        } finally {
            permits.release()
        }
    }
}
