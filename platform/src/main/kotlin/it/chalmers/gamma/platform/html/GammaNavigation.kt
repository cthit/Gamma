package it.chalmers.gamma.platform.html

import java.net.URI

data class GammaNavigationItem(
    val path: String,
    val label: String,
) {
    init {
        requireLocalApplicationPath(path, "Gamma navigation paths must be absolute application paths")
        require(label.isNotBlank()) { "Gamma navigation labels must not be blank" }
    }
}

class GammaNavigation(
    userItems: List<GammaNavigationItem>,
    adminItems: List<GammaNavigationItem>,
) {
    val userItems: List<GammaNavigationItem> = userItems.toList()
    val adminItems: List<GammaNavigationItem> = adminItems.toList()
}

data class GammaPageAssets(
    val favicon: String = "/img/itlogo.svg",
    val stylesheets: List<String> =
        listOf(
            "/webjars/picocss__pico/2.0.6/css/pico.green.min.css",
            "/css/main.css",
        ),
    val noNavigationStylesheet: String = "/css/no-nav.css",
    val scripts: List<String> =
        listOf(
            "/webjars/htmx.org/1.9.12/dist/htmx.min.js",
            "/webjars/htmx.org/1.9.12/dist/ext/loading-states.js",
            "/webjars/htmx.org/1.9.12/dist/ext/response-targets.js",
            "/webjars/hyperscript.org/0.9.12/dist/_hyperscript.min.js",
            "/webjars/sortablejs/1.15.3/Sortable.min.js",
            "/js/reorder.js",
            "/js/posts-sortable.js",
        ),
    val navigationImage: String = "/img/digit18.svg",
) {
    init {
        listOf(favicon, noNavigationStylesheet, navigationImage)
            .plus(stylesheets)
            .plus(scripts)
            .forEach { path ->
                requireLocalApplicationPath(path, "Gamma page assets must use absolute application paths")
            }
    }
}

private fun requireLocalApplicationPath(
    path: String,
    message: String,
) {
    val parsedPath = runCatching { URI(path) }.getOrNull()
    require(
        path.startsWith('/') &&
            !path.startsWith("//") &&
            '\\' !in path &&
            path.none { it.code in 0..31 || it.code == 127 } &&
            parsedPath != null &&
            !parsedPath.isOpaque &&
            parsedPath.scheme == null &&
            parsedPath.rawAuthority == null,
    ) {
        message
    }
}
