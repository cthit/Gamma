package it.chalmers.gamma.users

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class UserTypesTest {
    @Test
    fun `cid accepts four to twelve lowercase letters`() {
        assertEquals("mscott", Cid("mscott").value)
        assertEquals("abcd", Cid("abcd").value)
        assertEquals("abcdefghijkl", Cid("abcdefghijkl").value)
    }

    @Test
    fun `cid rejects invalid case characters and lengths`() {
        listOf("abc", "abcdefghijklm", "m.scott", "MScott", "michael1").forEach { invalid ->
            assertFailsWith<IllegalArgumentException>(invalid) { Cid(invalid) }
        }
    }

    @Test
    fun `email preserves the legacy domain suffix boundary`() {
        assertEquals("michael.scott@example.org", Email("michael.scott@example.org").value)
        assertFailsWith<IllegalArgumentException> { Email("michael.scott@example.technology") }
    }

    @Test
    fun `email rejects malformed and html-bearing values`() {
        listOf("missing-at.example.org", "a@b", "<script>@example.org").forEach { invalid ->
            assertFailsWith<IllegalArgumentException>(invalid) { Email(invalid) }
        }
    }

    @Test
    fun `email enforces the database length boundary`() {
        assertEquals(100, Email("a".repeat(88) + "@example.org").value.length)
        assertFailsWith<IllegalArgumentException> { Email("a".repeat(89) + "@example.org") }
    }

    @Test
    fun `directory pages own their user list`() {
        val user =
            DirectoryUser(
                UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"),
                Cid("mscott"),
                Nick("Boss"),
                FirstName("Michael"),
                LastName("Scott"),
                AcceptanceYear.of(2020, currentYear = 2026),
                version = 1,
                locked = false,
            )
        val submitted = mutableListOf(user)
        val page = DirectoryUserPage(submitted, nextCid = null)

        submitted.clear()

        assertEquals(listOf(user), page.users)
    }

    @Test
    fun `profile text rejects html-sensitive punctuation and preserves control characters`() {
        assertEquals("Pam-Pam", Nick("Pam-Pam").value)
        listOf("D'Arcy", "Research & Development", "\"Ace\"").forEach { invalid ->
            assertFailsWith<IllegalArgumentException> { LastName(invalid) }
        }
        assertEquals("Line\nBreak", FirstName("Line\nBreak").value)
        assertFailsWith<IllegalArgumentException> { FirstName("") }
        assertFailsWith<IllegalArgumentException> { LastName("a".repeat(51)) }
    }

    @Test
    fun `every profile text field enforces both length boundaries`() {
        listOf<(String) -> Any>(::Nick, ::FirstName, ::LastName).forEach { constructor ->
            assertEquals(
                50,
                when (val value = constructor("a".repeat(50))) {
                    is Nick -> value.value.length
                    is FirstName -> value.value.length
                    is LastName -> value.value.length
                    else -> error("unexpected value")
                },
            )
            assertFailsWith<IllegalArgumentException> { constructor("") }
            assertFailsWith<IllegalArgumentException> { constructor("a".repeat(51)) }
            constructor("control\u0000character")
        }
    }

    @Test
    fun `acceptance year uses an explicit year in deterministic tests`() {
        assertEquals(2026, AcceptanceYear.of(2026, currentYear = 2026).value)
        assertFailsWith<IllegalArgumentException> {
            AcceptanceYear.of(2027, currentYear = 2026)
        }
        assertFailsWith<IllegalArgumentException> {
            AcceptanceYear.of(2000, currentYear = 2026)
        }
    }

    @Test
    fun `password values never reveal secrets`() {
        val plainText = PlainTextPassword("password1337")
        val hash = PasswordHash("{bcrypt}${'$'}2y${'$'}10${'$'}abcdefghijklmnopqrstuv")

        assertEquals("<value redacted>", plainText.toString())
        assertEquals("<value redacted>", hash.toString())
        assertFailsWith<IllegalArgumentException> { PlainTextPassword("short") }
        assertEquals("é".repeat(37), PlainTextPassword("é".repeat(37)).value)
        assertFailsWith<IllegalArgumentException> { PasswordHash("plaintext") }
    }

    @Test
    fun `user identifiers parse valid uuids and reject malformed values`() {
        val raw = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"

        assertEquals(raw, UserId.parse(raw).value.toString())
        assertNotEquals(UserId.generate(), UserId.generate())
        assertFailsWith<IllegalArgumentException> { UserId.parse("not-a-uuid") }
    }

    @Test
    fun `identity values and projections redact personal data from diagnostics`() {
        val profile =
            UserProfile(
                id = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"),
                cid = Cid("mscott"),
                nick = Nick("Boss"),
                firstName = FirstName("Michael"),
                lastName = LastName("Scott"),
                acceptanceYear = AcceptanceYear.of(2020, currentYear = 2026),
                language = Language.EN,
                email = Email("michael.scott@example.org"),
                version = 1,
                locked = false,
                avatarUri = "/uploads/private-avatar.webp",
            )
        val update =
            MyProfileUpdate(
                profile.nick,
                profile.firstName,
                profile.lastName,
                profile.language,
                profile.email,
                expectedVersion = profile.version,
            )
        assertEquals("MyProfileUpdate(<redacted>)", update.toString())
        val rendered =
            listOf(
                profile.id,
                profile.cid,
                profile.nick,
                profile.firstName,
                profile.lastName,
                profile.email,
                profile.acceptanceYear,
                profile,
                update,
                DirectoryUser(
                    profile.id,
                    profile.cid,
                    profile.nick,
                    profile.firstName,
                    profile.lastName,
                    profile.acceptanceYear,
                    profile.version,
                    profile.locked,
                ),
                UserAccessFlag(
                    profile.id,
                    profile.firstName,
                    profile.nick,
                    profile.lastName,
                    enabled = true,
                ),
                DirectoryUserPageRequest(
                    query = "private-search",
                    afterCid = profile.cid,
                    scope = DirectoryUserScope.visibleToUser(profile.id),
                ),
                StoredUserAvatar("private-avatar-storage-id.png"),
            ).joinToString()

        listOf(
            profile.id.value.toString(),
            profile.cid.value,
            profile.nick.value,
            profile.firstName.value,
            profile.lastName.value,
            profile.email.value,
            checkNotNull(profile.avatarUri),
            profile.acceptanceYear.value.toString(),
            checkNotNull(profile.language).name,
            "private-search",
            "private-avatar-storage-id.png",
        ).forEach { personalValue ->
            assertFalse(rendered.contains(personalValue), "Diagnostics exposed $personalValue")
        }
    }
}
