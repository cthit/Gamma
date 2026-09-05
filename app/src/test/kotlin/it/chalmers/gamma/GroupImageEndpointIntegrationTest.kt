package it.chalmers.gamma

import it.chalmers.gamma.media.DefaultMedia
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.GroupImageKind
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.NewGroup
import it.chalmers.gamma.organization.NewGroupMembership
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import org.springframework.beans.factory.annotation.Autowired
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GroupImageEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var organizations: OrganizationQueries

    @Autowired
    private lateinit var creation: CreateGroup

    @Autowired
    private lateinit var deletion: DeleteGroup

    @Autowired
    private lateinit var images: GroupImages

    @Autowired
    private lateinit var media: MediaStore

    @Test
    fun `member uploads reads and deletes avatar and banner through HTTP`() {
        val actor = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        val groupId =
            creation.create(
                actor,
                NewGroup(
                    OrganizationName("http-group-images"),
                    PrettyName("HTTP group images"),
                    SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb"),
                ),
                listOf(
                    NewGroupMembership(
                        UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195"),
                        PostId.parse("7bb1db15-730d-4864-bfc3-99abe7c0ccf8"),
                        UnofficialPostName(null),
                    ),
                ),
            )
        try {
            val address = uniqueAddress()
            val browser = browser(address)
            assertEquals(302, browser.login("jhalpert").status)
            val (_, csrf) = browser.csrf("/")
            val client = HttpClient.newHttpClient()
            for (kind in listOf("avatar", "banner")) {
                val path = "/groups/$kind/${groupId.value}"
                val boundary = "gamma-image-boundary"
                val prefix =
                    "--$boundary\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"image.png\"\r\n" +
                        "Content-Type: image/png\r\n\r\n"
                val body = prefix.toByteArray() + imageBytes + "\r\n--$boundary--\r\n".toByteArray()
                val request =
                    HttpRequest
                        .newBuilder(URI.create(browser.baseUrl + path))
                        .header("Cookie", assertNotNull(browser.sessionCookie))
                        .header("X-Forwarded-For", address)
                        .header("X-CSRF-TOKEN", csrf)
                        .header("Content-Type", "multipart/form-data; boundary=$boundary")
                        .PUT(HttpRequest.BodyPublishers.ofByteArray(body))
                        .build()
                assertEquals(204, client.send(request, HttpResponse.BodyHandlers.ofByteArray()).statusCode())
                val group = assertNotNull(organizations.findGroup(groupId))
                val pointer = assertNotNull(if (kind == "avatar") group.avatarUri else group.bannerUri)
                val fallback = if (kind == "avatar") DefaultMedia.GROUP_AVATAR else DefaultMedia.GROUP_BANNER
                val imageRequest =
                    HttpRequest
                        .newBuilder(URI.create("${browser.baseUrl}/images/group/$kind/${groupId.value}"))
                        .header(
                            "Cookie",
                            assertNotNull(browser.sessionCookie),
                        ).header("X-Forwarded-For", address)
                        .GET()
                        .build()
                val downloaded = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray())
                assertEquals(200, downloaded.statusCode())
                assertContentEquals(media.read(MediaUri(pointer), fallback).bytes, downloaded.body())

                assertEquals(204, browser.form("DELETE", path, mapOf("_csrf" to csrf)).status)
                val cleared = assertNotNull(organizations.findGroup(groupId))
                assertNull(if (kind == "avatar") cleared.avatarUri else cleared.bannerUri)
                val defaultImage = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray())
                assertEquals(200, defaultImage.statusCode())
                assertContentEquals(media.read(null, fallback).bytes, defaultImage.body())
            }
        } finally {
            images.delete(actor, groupId, GroupImageKind.AVATAR)
            images.delete(actor, groupId, GroupImageKind.BANNER)
            deletion.delete(actor, groupId)
        }
    }

    private companion object {
        val imageBytes: ByteArray =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            )
    }
}
