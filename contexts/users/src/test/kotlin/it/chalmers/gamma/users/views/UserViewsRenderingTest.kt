package it.chalmers.gamma.users.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.WebViewer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class UserViewsRenderingTest {
    @Test
    fun `forms escape user content and include csrf`() {
        val html =
            renderChangePassword(
                GammaPageContext(WebViewer("<script>alert('viewer')</script>", false), "csrf-token"),
                "<script>alert('message')</script>",
            )

        assertContains(html, "&lt;script&gt;alert('viewer')&lt;/script&gt;")
        assertContains(html, "&lt;script&gt;alert('message')&lt;/script&gt;")
        assertFalse(html.contains("<script>alert"))
        assertContains(html, "name=\"_csrf\"")
        assertContains(html, "value=\"csrf-token\"")
    }
}
