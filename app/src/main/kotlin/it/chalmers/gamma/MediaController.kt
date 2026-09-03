package it.chalmers.gamma

import it.chalmers.gamma.media.DefaultMedia
import it.chalmers.gamma.media.MediaContent
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserStore
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ThreadLocalRandom

@RestController
class MediaController(
    private val media: MediaStore,
    private val users: UserStore,
    private val organizations: OrganizationStore,
) {
    @GetMapping("/images/user/avatar/{id}")
    fun userAvatar(
        @PathVariable id: String,
    ): ResponseEntity<ByteArray> {
        val avatarUri = users.findUser(UserId.parse(id))?.avatarUri
        return media.read(avatarUri.asMediaUri(), DefaultMedia.USER_AVATAR).response()
    }

    @GetMapping("/images/group/avatar/{id}")
    fun groupAvatar(
        @PathVariable id: String,
    ): ResponseEntity<ByteArray> {
        val group = requireNotNull(organizations.findGroup(GroupId.parse(id))) { "Group does not exist" }
        return media.read(group.avatarUri.asMediaUri(), DefaultMedia.GROUP_AVATAR).response()
    }

    @GetMapping("/images/group/banner/{id}")
    fun groupBanner(
        @PathVariable id: String,
    ): ResponseEntity<ByteArray> {
        val group = requireNotNull(organizations.findGroup(GroupId.parse(id))) { "Group does not exist" }
        return media.read(group.bannerUri.asMediaUri(), DefaultMedia.GROUP_BANNER).response()
    }

    @GetMapping("/images/super-group/avatar/{id}")
    fun superGroupAvatar(
        @PathVariable id: String,
    ): ResponseEntity<ByteArray> = superGroupImage(id, Group::avatarUri, DefaultMedia.GROUP_AVATAR)

    @GetMapping("/images/super-group/banner/{id}")
    fun superGroupBanner(
        @PathVariable id: String,
    ): ResponseEntity<ByteArray> = superGroupImage(id, Group::bannerUri, DefaultMedia.GROUP_BANNER)

    private fun superGroupImage(
        id: String,
        image: (Group) -> String?,
        fallback: DefaultMedia,
    ): ResponseEntity<ByteArray> {
        val candidates = organizations.listGroups(SuperGroupId.parse(id)).mapNotNull(image)
        val uri =
            candidates
                .takeIf(
                    List<String>::isNotEmpty,
                )?.let { it[ThreadLocalRandom.current().nextInt(it.size)] }
        return media.read(uri.asMediaUri(), fallback).response()
    }
}

private fun String?.asMediaUri(): MediaUri? = this?.let { runCatching { MediaUri(it) }.getOrNull() }

private fun MediaContent.response(): ResponseEntity<ByteArray> =
    ResponseEntity
        .ok()
        .contentType(MediaType.parseMediaType(contentType))
        .body(bytes)
