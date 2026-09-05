// Spring response signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.users.UserAvatarUpload
import it.chalmers.gamma.users.UserAvatars
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class UserAvatarController(
    private val avatars: UserAvatars,
) {
    @PutMapping("/me/avatar")
    fun uploadAvatar(
        authentication: Authentication,
        @RequestParam file: MultipartFile,
    ): ResponseEntity<Void> {
        require(!file.isEmpty) { "Choose an image to upload" }
        avatars.replaceMyAvatar(authentication.actor(), UserAvatarUpload(file.bytes, file.contentType))
        return ResponseEntity.noContent().build()
    }
}
