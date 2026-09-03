package it.chalmers.gamma.platform.html

import kotlinx.html.BODY
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.HEADER
import kotlinx.html.MAIN
import kotlinx.html.NAV
import kotlinx.html.a
import kotlinx.html.aside
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.hr
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.lang
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.nav
import kotlinx.html.progress
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul

data class WebViewer(
    val nick: String,
    val isAdmin: Boolean,
) {
    override fun toString(): String = "WebViewer(nick=<redacted>, isAdmin=$isAdmin)"
}

data class GammaPageContext(
    val viewer: WebViewer? = null,
    val csrfToken: String? = null,
    val contextPath: String = "",
    val navigation: GammaNavigation? = null,
    val showNavigation: Boolean = true,
    val assets: GammaPageAssets = GammaPageAssets(),
)

fun gammaPage(
    title: String,
    page: GammaPageContext = GammaPageContext(),
    content: MAIN.() -> Unit,
): String =
    createHTML().html {
        lang = "en"
        head {
            title { +if (title == "Gamma") title else "$title - Gamma" }
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            meta(name = "color-scheme", content = "light dark")
            link(rel = "shortcut icon", type = "image/svg+xml", href = page.contextPath + page.assets.favicon)
            page.assets.stylesheets.forEach { stylesheet ->
                link(rel = "stylesheet", href = page.contextPath + stylesheet)
            }
            if (!page.showNavigation) {
                link(rel = "stylesheet", href = page.contextPath + page.assets.noNavigationStylesheet)
            }
            meta(name = "htmx-config", content = HTMX_CONFIGURATION)
            page.assets.scripts.forEach { source -> script(src = page.contextPath + source) {} }
        }
        body(classes = "container-fluid") {
            configureHtmx()
            renderChrome(page)
            main { content() }
        }
    }

private fun BODY.configureHtmx() {
    // Controllers return complete pages after ordinary navigation and form submissions.
    // Explicit data-hx-* controls below still opt individual partial requests into htmx.
    attributes["hx-boost"] = "false"
    attributes["hx-history"] = "false"
    attributes["hx-ext"] = "loading-states, response-targets"
    attributes["hx-target-409"] = "body"
    attributes["_"] = "on htmx:afterSwap remove .show-nav from document.body"
    attributes["data-loading-delay"] = "500"
}

private fun BODY.renderChrome(page: GammaPageContext) {
    div { id = "alerts" }
    div {
        id = "loader-container"
        attributes["data-loading"] = ""
        attributes["data-loading-class"] = "loading"
        progress {}
    }
    if (page.showNavigation && page.navigation != null) {
        nav {
            renderNavigationItems(
                page.navigation,
                page.viewer?.isAdmin == true,
                page.contextPath + page.assets.navigationImage,
            )
        }
    }
    header { renderHeader(page.viewer, page.csrfToken, page.contextPath) }
}

private fun HEADER.renderHeader(
    viewer: WebViewer?,
    csrfToken: String?,
    contextPath: String,
) {
    button(classes = "outline contrast") {
        id = "toggle-nav"
        attributes["_"] = "on click toggle .show-nav on document.body"
        +"Menu"
    }
    div { a(href = "$contextPath/") { h1 { +"Gamma" } } }
    div {
        if (viewer != null) {
            div { +"Hey, ${viewer.nick}!" }
            form(action = "$contextPath/logout", method = FormMethod.post) {
                csrfInput(checkNotNull(csrfToken) { "Authenticated pages require a CSRF token" })
                button(classes = "outline contrast") {
                    attributes["data-loading-disable"] = ""
                    +"Logout"
                }
            }
        }
    }
}

private fun NAV.renderNavigationItems(
    navigation: GammaNavigation,
    isAdmin: Boolean,
    navigationImage: String,
) {
    aside {
        ul {
            navigation.userItems.forEach { item -> li { a(href = item.path) { +item.label } } }
            if (isAdmin) {
                hr {}
                navigation.adminItems.forEach { item -> li { a(href = item.path) { +item.label } } }
            }
            hr {}
            li { a(href = "https://github.com/cthit/gamma", target = "_blank") { +"GitHub" } }
            li {
                +"Made by "
                span { a(href = "https://digit.chalmers.it") { +"digIT" } }
            }
            li { img(src = navigationImage, alt = "digit18 smurf") { id = "digit18-smurf" } }
        }
    }
}

private val HTMX_CONFIGURATION =
    """
    {
      "selfRequestsOnly": true,
      "allowScriptTags": false,
      "historyCacheSize": 0,
      "allowEval": false,
      "useTemplateFragments": true,
      "includeIndicatorStyles": false
    }
    """.trimIndent()
