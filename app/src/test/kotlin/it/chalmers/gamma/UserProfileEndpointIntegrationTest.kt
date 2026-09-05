package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.UpdateUser
import it.chalmers.gamma.users.UserProfile
import it.chalmers.gamma.users.UserQueries
import it.chalmers.gamma.users.UserUpdate
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserProfileEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var updates: UpdateUser

    @Test
    fun `administrator profile form persists submitted fields rejects stale edits and denies members`() {
        val previous = assertNotNull(users.findUser(Cid("jhalpert")))
        try {
            val admin = browser(uniqueAddress())
            assertEquals(302, admin.login().status)
            val (_, csrf) = admin.csrf("/users/${previous.id.value}/edit")
            val fields =
                mapOf(
                    "nick" to "Admin edited",
                    "firstName" to previous.firstName.value,
                    "lastName" to previous.lastName.value,
                    "acceptanceYear" to "2021",
                    "language" to "EN",
                    "email" to "ADMIN.EDIT@EXAMPLE.ORG",
                    "version" to previous.version.toString(),
                    "_csrf" to csrf,
                )
            val path = "/users/${previous.id.value}"
            val response = admin.form("PUT", path, fields)
            assertEquals(302, response.status)
            assertEquals("$path?updated=true", response.header("Location"))
            val saved = assertNotNull(users.findUser(previous.id))
            assertEquals("Admin edited", saved.nick.value)
            assertEquals(2021, saved.acceptanceYear.value)
            assertEquals("admin.edit@example.org", saved.email.value)
            assertEquals(previous.version + 1, saved.version)
            assertEquals(previous.locked, saved.locked)
            val details = admin.get(path)
            assertEquals(200, details.status)
            assertContains(details.body, "Admin edited")
            val listed = admin.get("/users")
            assertEquals(200, listed.status)
            assertContains(listed.body, "Admin edited")
            assertEquals(404, admin.get("/users/${it.chalmers.gamma.users.UserId.generate().value}").status)
            assertEquals(409, admin.form("PUT", path, fields + ("nick" to "Stale")).status)
            assertEquals(saved, users.findUser(previous.id))

            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, memberCsrf) = member.csrf("/me/edit")
            assertEquals(403, member.get("/users").status)
            assertEquals(403, member.get(path).status)
            assertEquals(403, member.get("$path/edit").status)
            assertEquals(403, member.form("PUT", path, fields + ("_csrf" to memberCsrf)).status)
            assertEquals(saved, users.findUser(previous.id))
        } finally {
            restore(previous)
        }
    }

    @Test
    fun `personal profile and email forms preserve protected fields and reject stale and duplicate writes`() {
        val previous = assertNotNull(users.findUser(Cid("jhalpert")))
        try {
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, csrf) = member.csrf("/me/edit")
            val fields =
                mapOf(
                    "nick" to "Personal edited",
                    "firstName" to previous.firstName.value,
                    "lastName" to previous.lastName.value,
                    "email" to "PERSONAL.EDIT@EXAMPLE.ORG",
                    "language" to "EN",
                    "version" to previous.version.toString(),
                    "_csrf" to csrf,
                    "acceptanceYear" to "1900",
                    "locked" to "true",
                    "cid" to "mscott",
                )
            val response = member.form("PUT", "/me", fields)
            assertEquals(302, response.status)
            assertEquals("/?updated=true", response.header("Location"))
            val edited = assertNotNull(users.findUser(previous.id))
            assertEquals(Nick("Personal edited"), edited.nick)
            assertEquals(previous.cid, edited.cid)
            assertEquals(previous.acceptanceYear, edited.acceptanceYear)
            assertEquals(previous.locked, edited.locked)
            assertEquals(previous.version + 1, edited.version)
            for (path in listOf("/me", "/me/edit", "/me/cancel-edit")) {
                val page = member.get(path)
                assertEquals(200, page.status)
                assertContains(page.body, "Personal edited")
            }

            val emailResponse =
                member.form(
                    "PUT",
                    "/me/email",
                    mapOf(
                        "email" to "ONLY.EMAIL@EXAMPLE.ORG",
                        "_csrf" to csrf,
                    ),
                )
            assertEquals(302, emailResponse.status)
            assertEquals("/?updated=true", emailResponse.header("Location"))
            val saved = users.findUser(previous.id)
            assertEquals(edited.copy(email = Email("only.email@example.org"), version = edited.version + 1), saved)
            assertEquals(409, member.form("PUT", "/me", fields + ("version" to edited.version.toString())).status)

            val other = assertNotNull(users.findUser(Cid("mscott")))
            val duplicate =
                member.form(
                    "PUT",
                    "/me/email",
                    mapOf(
                        "email" to other.email.value.uppercase(),
                        "_csrf" to csrf,
                    ),
                )
            assertEquals(409, duplicate.status)
            assertEquals(saved, users.findUser(previous.id))
        } finally {
            restore(previous)
        }
    }

    private fun restore(previous: UserProfile) {
        val current = assertNotNull(users.findUser(previous.id))
        val administrator = assertNotNull(users.findUser(Cid("mscott")))
        updates.update(
            Actor.User(ActorUserId(administrator.id.value), true),
            UserUpdate(
                previous.id,
                current.version,
                previous.nick,
                previous.firstName,
                previous.lastName,
                previous.acceptanceYear,
                previous.language,
                previous.email,
            ),
        )
    }
}
