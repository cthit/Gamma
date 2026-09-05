package it.chalmers.gamma

import it.chalmers.gamma.media.DefaultMedia
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.users.UserAvatars
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserAvatarEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var avatars: UserAvatars

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Autowired
    private lateinit var media: MediaStore

    @Test
    fun `student uploads replaces and reads an avatar through HTTP`() {
        val administrator = browser(uniqueAddress())
        assertEquals(302, administrator.login().status)
        val (_, adminCsrf) = administrator.csrf("/users/create")
        val created =
            administrator.form(
                "POST",
                "/users/create",
                mapOf(
                    "cid" to "avataruser",
                    "nick" to "Avatar student",
                    "firstName" to "Avatar",
                    "lastName" to "Student",
                    "acceptanceYear" to "2021",
                    "language" to "EN",
                    "email" to "avatar.user@example.org",
                    "password" to "password1337",
                    "_csrf" to adminCsrf,
                ),
            )
        assertEquals(302, created.status)
        val userId = UserId.parse(assertNotNull(created.header("Location")).substringAfterLast('/'))
        val actor = Actor.User(ActorUserId(userId.value), false)
        try {
            val address = uniqueAddress()
            val browser = browser(address)
            assertEquals(302, browser.login("avataruser").status)
            val (_, csrf) = browser.csrf("/me")
            val client = HttpClient.newHttpClient()
            val boundary = "gamma-user-avatar-boundary"
            val prefix =
                "--$boundary\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"image.png\"\r\n" +
                    "Content-Type: image/png\r\n\r\n"
            val body = prefix.toByteArray() + imageBytes + "\r\n--$boundary--\r\n".toByteArray()
            val request =
                HttpRequest
                    .newBuilder(URI.create(browser.baseUrl + "/me/avatar"))
                    .header("Cookie", assertNotNull(browser.sessionCookie))
                    .header("X-Forwarded-For", address)
                    .header("X-CSRF-TOKEN", csrf)
                    .header("Content-Type", "multipart/form-data; boundary=$boundary")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build()
            val imageRequest =
                HttpRequest
                    .newBuilder(URI.create("${browser.baseUrl}/images/user/avatar/${userId.value}"))
                    .header("Cookie", assertNotNull(browser.sessionCookie))
                    .header("X-Forwarded-For", address)
                    .GET()
                    .build()
            var previous: String? = null
            repeat(2) {
                assertEquals(204, client.send(request, HttpResponse.BodyHandlers.ofByteArray()).statusCode())
                val current = assertNotNull(users.findUser(userId)?.avatarUri)
                val downloaded = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray())
                assertEquals(200, downloaded.statusCode())
                assertContentEquals(imageBytes, downloaded.body())
                assertEquals("image/png", downloaded.headers().firstValue("Content-Type").orElseThrow())
                previous?.let { old -> assertFails { media.read(MediaUri(old), DefaultMedia.USER_AVATAR) } }
                previous = current
            }
            avatars.deleteMyAvatar(actor)
            assertNull(users.findUser(userId)?.avatarUri)
            val fallback = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray())
            assertEquals(200, fallback.statusCode())
            assertContentEquals(media.read(null, DefaultMedia.USER_AVATAR).bytes, fallback.body())
        } finally {
            avatars.deleteMyAvatar(actor)
            deletion.delete(
                AccountDeletion.Administrator(
                    deletionTestAdministrator,
                    userId,
                ),
            )
        }
    }

    private companion object {
        val imageBytes: ByteArray =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            )
    }
}
