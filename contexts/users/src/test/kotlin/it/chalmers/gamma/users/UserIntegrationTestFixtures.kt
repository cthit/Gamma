package it.chalmers.gamma.users

internal const val FIXTURE_ADMINISTRATOR_CID = "mscott"
internal val FIXTURE_ADMINISTRATOR_ID = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")

internal object AlwaysMatchingPasswordHasher : PasswordHasher {
    private val hash = PasswordHash("{bcrypt}\$immediate")

    override fun hash(password: PlainTextPassword): PasswordHash = hash

    override fun verify(
        password: PlainTextPassword,
        hash: PasswordHash,
    ): Boolean = true

    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean = false
}

internal fun Int.toBase26(): String {
    var remaining = this
    return buildString {
        do {
            append(('a'.code + remaining % 26).toChar())
            remaining /= 26
        } while (remaining > 0)
    }.reversed()
}
