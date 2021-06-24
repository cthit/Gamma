package it.chalmers.gamma.app.groupimages.controller;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.GroupImages;
import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.app.group.service.GroupService;
import it.chalmers.gamma.app.groupimages.service.GroupImagesService;
import it.chalmers.gamma.util.component.ImageService;
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

@RestController
@RequestMapping("/internal/groups")
public class GroupImagesController {

    private final GroupService groupService;
    private final GroupImagesService groupImagesService;
    private final ImageService imageService;

    public GroupImagesController(GroupService groupService,
                                 GroupImagesService groupImagesService,
                                 ImageService imageService) {
        this.groupService = groupService;
        this.groupImagesService = groupImagesService;
        this.imageService = imageService;
    }

    @PutMapping("/avatar/{id}")
    public GroupAvatarEdited editGroupAvatar(@RequestParam MultipartFile file, @PathVariable("id") GroupId id) {
        try {
            this.groupImagesService.editGroupAvatar(id, file);
            return new GroupAvatarEdited();
        } catch (ImageService.FileCouldNotBeRemovedException
                | ImageService.FileContentNotValidException
                | ImageService.FileCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
    }

    @PutMapping("/banner/{id}")
    public GroupBannerEdited editGroupBanner(@RequestParam MultipartFile file, @PathVariable("id") GroupId id) {
        try {
            this.groupImagesService.editGroupBanner(id, file);
            return new GroupBannerEdited();
        } catch (ImageService.FileCouldNotBeRemovedException
                | ImageService.FileContentNotValidException
                | ImageService.FileCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getGroupAvatar(@PathVariable("id") GroupId id) throws IOException {
        ImageUri uri = groupImagesService.getGroupImages(id).map(GroupImages::avatarUri)
                .orElse(ImageUri.valueOf("default_group_avatar.jpg"));
        return ResponseEntity
                .ok()
                .contentType(getContentType(uri))
                .body(imageService.getData(uri));
    }

    @GetMapping("/banner/{id}")
    public ResponseEntity<byte[]> getGroupBanner(@PathVariable("id") GroupId id) throws IOException {
        ImageUri uri = groupImagesService.getGroupImages(id).map(GroupImages::bannerUri)
                .orElse(ImageUri.valueOf("default_group_banner.jpg"));
        return ResponseEntity
                .ok()
                .contentType(getContentType(uri))
                .body(imageService.getData(uri));
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
