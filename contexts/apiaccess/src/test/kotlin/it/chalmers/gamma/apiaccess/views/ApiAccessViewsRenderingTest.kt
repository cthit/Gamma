package it.chalmers.gamma.apiaccess.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.WebViewer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class ApiAccessViewsRenderingTest {
    @Test
    fun `forms escape user content and include csrf`() {
        val html =
            renderCreateApiKey(
                GammaPageContext(WebViewer("<script>alert('viewer')</script>", true), "csrf-token"),
            )

        assertContains(html, "&lt;script&gt;alert('viewer')&lt;/script&gt;")
        assertFalse(html.contains("<script>alert"))
        assertContains(html, "name=\"_csrf\"")
        assertContains(html, "value=\"csrf-token\"")
    }
}
