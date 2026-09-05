package it.chalmers.gamma

import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CreateUserEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `administrator form creates a user who can log in and rejects duplicate and unprivileged creation`() {
        val admin = browser(uniqueAddress())
        assertEquals(302, admin.login().status)
        val (_, csrf) = admin.csrf("/users/create")
        val fields =
            mapOf(
                "cid" to "createduser",
                "nick" to "Created student",
                "firstName" to "Created",
                "lastName" to "Student",
                "acceptanceYear" to "2021",
                "language" to "EN",
                "email" to "CREATED.USER@EXAMPLE.ORG",
                "password" to "password1337",
                "_csrf" to csrf,
            )
        val response = admin.form("POST", "/users/create", fields)
        assertEquals(302, response.status)
        val userId = UserId.parse(assertNotNull(response.header("Location")).substringAfterLast('/'))
        try {
            val saved = assertNotNull(users.findUser(userId))
            assertEquals(Cid("createduser"), saved.cid)
            assertEquals("created.user@example.org", saved.email.value)
            assertEquals(0, saved.version)
            assertEquals(409, admin.form("POST", "/users/create", fields).status)

            val member = browser(uniqueAddress())
            assertEquals(302, member.login("createduser").status)
            assertEquals(200, member.get("/me").status)
            val (_, memberCsrf) = member.csrf("/me/edit")
            val denied =
                member.form(
                    "POST",
                    "/users/create",
                    fields + mapOf("cid" to "denieduser", "_csrf" to memberCsrf),
                )
            assertEquals(403, denied.status)
            assertNull(users.findUser(Cid("denieduser")))
        } finally {
            deletion.delete(
                AccountDeletion.Administrator(
                    deletionTestAdministrator,
                    userId,
                ),
            )
        }
    }
}
