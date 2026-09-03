package it.chalmers.gamma.organization.views

import it.chalmers.gamma.organization.EmailPrefix
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.organization.Membership
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PostOrder
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.WebViewer
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.DirectoryUser
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.UserId
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class OrganizationViewsRenderingTest {
    @Test
    fun `forms escape user content and include csrf`() {
        val html =
            renderGroupEditor(
                GammaPageContext(WebViewer("<script>alert('viewer')</script>", true), "csrf-token"),
                GroupEditor(emptyList()),
            )

        assertContains(html, "&lt;script&gt;alert('viewer')&lt;/script&gt;")
        assertFalse(html.contains("<script>alert"))
        assertContains(html, "name=\"_csrf\"")
        assertContains(html, "value=\"csrf-token\"")
    }

    @Test
    fun `organization pages render representative data and admin actions`() {
        val page = GammaPageContext(WebViewer("Admin", true), "csrf-token", "/gamma")
        val type = SuperGroupType("society")
        val superGroup =
            SuperGroup(
                SuperGroupId.generate(),
                version = 2,
                OrganizationName("diggit"),
                PrettyName("DIGIT"),
                type,
                LocalizedText.of(sv = "Datagruppen", en = "The data group"),
            )
        val group =
            Group(
                GroupId.generate(),
                version = 3,
                OrganizationName("webdev"),
                PrettyName("Web developers"),
                superGroup,
                avatarUri = null,
                bannerUri = null,
            )
        val post =
            Post(
                PostId.generate(),
                version = 4,
                LocalizedText.of(sv = "Ordförande", en = "Chair"),
                EmailPrefix("chair"),
                PostOrder(0),
            )
        val userId = UserId.generate()
        val user =
            DirectoryUser(
                userId,
                Cid("alice"),
                Nick("Alice"),
                FirstName("Alice"),
                LastName("Andersson"),
                AcceptanceYear.of(2024, 2026),
                version = 5,
                locked = false,
            )
        val membership = Membership(userId, group.id, post.id, UnofficialPostName("Web chair"))

        val html =
            listOf(
                renderGroups(page, listOf(group)),
                renderGroupEditor(
                    page,
                    GroupEditor(listOf(superGroup), group, listOf(user), listOf(post), listOf(membership)),
                ),
                renderNewMember(listOf(user), listOf(post)),
                renderGroupDetails(
                    page,
                    GroupDetailsPage(group, listOf(membership), mapOf(userId to user), mapOf(post.id to post), userId),
                ),
                renderSuperGroups(page, listOf(superGroup)),
                renderSuperGroupEditor(page, listOf(type), superGroup),
                renderSuperGroupDetails(page, superGroup, emptyList()),
                renderPosts(page, listOf(post)),
                renderPostEditor(page, post),
                renderPostDetails(page, post),
                renderTypes(page, listOf(type)),
                renderTypeDetails(page, type, emptyList()),
            ).joinToString("\n")

        assertContains(html, "Web developers")
        assertContains(html, "Web chair")
        assertContains(html, "Alice")
        assertContains(html, "The data group")
        assertContains(html, "Chair")
        assertContains(html, "name=\"_method\"")
        assertContains(html, "value=\"csrf-token\"")
    }
}
