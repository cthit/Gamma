package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.html.GammaNavigation
import it.chalmers.gamma.platform.html.GammaNavigationItem
import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.WebViewer
import it.chalmers.gamma.users.GammaPrincipal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import java.util.UUID

internal fun Authentication.actor(): Actor.User {
    val principal = principal as GammaPrincipal
    return Actor.User(ActorUserId(UUID.fromString(principal.userId)), principal.administrator)
}

internal fun pageContext(
    authentication: Authentication?,
    csrfToken: CsrfToken,
    request: HttpServletRequest,
    showNavigation: Boolean = true,
): GammaPageContext {
    val principal = authentication?.principal as? GammaPrincipal
    return GammaPageContext(
        viewer = principal?.let { WebViewer(it.nick, it.administrator) },
        csrfToken = csrfToken.token,
        contextPath = request.contextPath,
        navigation = if (principal == null) null else applicationNavigation(request.contextPath),
        showNavigation = showNavigation,
    )
}

private fun applicationNavigation(contextPath: String) =
    GammaNavigation(
        userItems =
            listOf(
                GammaNavigationItem("$contextPath/", "Home"),
                GammaNavigationItem("$contextPath/users", "Users"),
                GammaNavigationItem("$contextPath/groups", "Groups"),
                GammaNavigationItem("$contextPath/super-groups", "Super groups"),
                GammaNavigationItem("$contextPath/posts", "Posts"),
                GammaNavigationItem("$contextPath/my-clients", "My clients"),
                GammaNavigationItem("$contextPath/user-agreement", "User agreement"),
                GammaNavigationItem("$contextPath/delete-your-account", "Delete account"),
            ),
        adminItems =
            listOf(
                GammaNavigationItem("$contextPath/api-keys", "Api keys"),
                GammaNavigationItem("$contextPath/clients", "Clients"),
                GammaNavigationItem("$contextPath/admins", "Admins"),
                GammaNavigationItem("$contextPath/gdpr", "GDPR"),
                GammaNavigationItem("$contextPath/allow-list", "Allow lists"),
                GammaNavigationItem("$contextPath/activation-codes", "Activation codes"),
                GammaNavigationItem("$contextPath/types", "Types"),
                GammaNavigationItem("$contextPath/throttling", "Throttling"),
            ),
    )
