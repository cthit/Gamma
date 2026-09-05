// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.organization.CreatePost
import it.chalmers.gamma.organization.DeletePost
import it.chalmers.gamma.organization.EmailPrefix
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.organization.NewPost
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PostUpdate
import it.chalmers.gamma.organization.ReorderPosts
import it.chalmers.gamma.organization.UpdatePost
import it.chalmers.gamma.organization.views.renderPostDetails
import it.chalmers.gamma.organization.views.renderPostEditor
import it.chalmers.gamma.organization.views.renderPosts
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val organizations: OrganizationQueries,
    private val postCreation: CreatePost,
    private val postUpdates: UpdatePost,
    private val postDeletion: DeletePost,
    private val postOrdering: ReorderPosts,
) {
    @GetMapping("/posts", produces = [MediaType.TEXT_HTML_VALUE])
    fun posts(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderPosts(pageContext(authentication, csrfToken, request), organizations.listPosts())

    @GetMapping("/posts/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createPostPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderPostEditor(pageContext(authentication, csrfToken, request))

    @PostMapping("/posts")
    fun createPost(
        authentication: Authentication,
        @RequestParam svName: String,
        @RequestParam enName: String,
        @RequestParam emailPrefix: String,
    ): ResponseEntity<Void> {
        val id =
            postCreation.create(
                authentication.actor(),
                NewPost(LocalizedText.of(svName, enName), EmailPrefix(emailPrefix)),
            )
        return redirect("/posts/${id.value}")
    }

    @GetMapping("/posts/{postId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun postDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable postId: String,
    ): ResponseEntity<String> {
        val post = organizations.findPost(PostId.parse(postId)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderPostDetails(pageContext(authentication, csrfToken, request), post))
    }

    @GetMapping("/posts/{postId}/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editPost(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable postId: String,
    ): ResponseEntity<String> {
        val post = organizations.findPost(PostId.parse(postId)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderPostEditor(pageContext(authentication, csrfToken, request), post))
    }

    @PutMapping("/posts/{postId}")
    fun updatePost(
        authentication: Authentication,
        @PathVariable postId: String,
        @ModelAttribute form: UpdatePostForm,
    ): ResponseEntity<Void> {
        postUpdates.update(
            authentication.actor(),
            PostUpdate(
                PostId.parse(postId),
                form.version,
                LocalizedText.of(form.svName, form.enName),
                EmailPrefix(form.emailPrefix),
            ),
        )
        return redirect("/posts/$postId")
    }

    @DeleteMapping("/posts/{postId}")
    fun deletePost(
        authentication: Authentication,
        @PathVariable postId: String,
    ): ResponseEntity<Void> {
        postDeletion.delete(authentication.actor(), PostId.parse(postId))
        return redirect("/posts")
    }

    @PutMapping("/posts/order")
    fun reorderPosts(
        authentication: Authentication,
        @RequestParam list: List<String>,
    ): ResponseEntity<Void> {
        postOrdering.reorder(authentication.actor(), list.map(PostId::parse))
        return ResponseEntity.ok().build()
    }
}

data class UpdatePostForm(
    val svName: String,
    val enName: String,
    val emailPrefix: String,
    val version: Int,
)
