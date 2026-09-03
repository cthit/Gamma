package it.chalmers.gamma.oauth.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.WebViewer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class OAuthViewsRenderingTest {
    @Test
    fun `forms escape user content and include csrf`() {
        val html =
            renderCreateClient(
                GammaPageContext(WebViewer("<script>alert('viewer')</script>", false), "csrf-token"),
                personal = true,
            )

        assertContains(html, "&lt;script&gt;alert('viewer')&lt;/script&gt;")
        assertFalse(html.contains("<script>alert"))
        assertContains(html, "name=\"_csrf\"")
        assertContains(html, "value=\"csrf-token\"")
    }
}
