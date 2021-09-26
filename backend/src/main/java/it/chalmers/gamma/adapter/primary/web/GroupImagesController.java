package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.common.ImageUri;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/internal/groups")
public final class GroupImagesController {

    private final GroupFacade groupFacade;

    public GroupImagesController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @PutMapping("/avatar/{id}")
    public GroupAvatarEdited editGroupAvatar(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
//        try {
//            this.groupImagesService.editGroupAvatar(id, file);
//            return new GroupAvatarEdited();
//        } catch (ImageService.ImageCouldNotBeRemovedException
//                | ImageService.ImageContentNotValidException
//                | ImageService.ImageCouldNotBeSavedException e) {
//            throw new FileIssueResponse();
//        }
        return null;
    }

    @PutMapping("/banner/{id}")
    public GroupBannerEdited editGroupBanner(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
//        try {
//            this.groupImagesService.editGroupBanner(id, file);
//            return new GroupBannerEdited();
//        } catch (ImageService.ImageCouldNotBeRemovedException
//                | ImageService.ImageContentNotValidException
//                | ImageService.ImageCouldNotBeSavedException e) {
//            throw new FileIssueResponse();
//        }
        return null;
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getGroupAvatar(@PathVariable("id") UUID id) throws IOException {
//        ImageUri uri = groupImagesService.getGroupImages(id).map(GroupImages::avatarUri)
//                .orElse(new ImageUri("default_group_avatar.jpg"));
//        return ResponseEntity
//                .ok()
//                .contentType(getContentType(uri))
//                .body(imageService.getData(uri));
        return null;
    }

    @GetMapping("/banner/{id}")
    public ResponseEntity<byte[]> getGroupBanner(@PathVariable("id") UUID id) throws IOException {
//        ImageUri uri = groupImagesService.getGroupImages(id).map(GroupImages::bannerUri)
//                .orElse(new ImageUri("default_group_banner.jpg"));
//        return ResponseEntity
//                .ok()
//                .contentType(getContentType(uri))
//                .body(imageService.getData(uri));
        return null;
    }

    private MediaType getContentType(ImageUri uri) {
        String[] uriData = uri.toString().split("\\.");
        String type = uriData[uriData.length - 1];
        return (type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                : type.equals("png") ? MediaType.IMAGE_PNG
                : MediaType.IMAGE_GIF);
    }

    private static class GroupBannerEdited extends SuccessResponse { }
    private static class GroupAvatarEdited extends SuccessResponse { }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
