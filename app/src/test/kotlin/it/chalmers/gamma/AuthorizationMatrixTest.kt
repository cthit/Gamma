package it.chalmers.gamma

import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorizationMatrixTest : SpringApplicationTest() {
    @Test
    fun `representative routes enforce anonymous user admin and owner access`() {
        val anonymous = browser(uniqueAddress())
        assertEquals(302, anonymous.get("/users").status)

        val user = browser(uniqueAddress())
        assertEquals(302, user.login("jhalpert").status)
        assertEquals(403, user.get("/users").status)
        assertEquals(200, user.get("/").status)
        assertEquals(200, user.get("/my-clients").status)

        val administrator = browser(uniqueAddress())
        assertEquals(302, administrator.login().status)
        assertEquals(200, administrator.get("/users").status)
    }
}
