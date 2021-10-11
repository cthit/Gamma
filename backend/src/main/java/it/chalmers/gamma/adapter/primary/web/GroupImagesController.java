package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.facade.ImageFacade;
import it.chalmers.gamma.app.service.ImageService;
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

    private final ImageFacade imageFacade;

    public GroupImagesController(ImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    @PutMapping("/avatar/{id}")
    public GroupAvatarEdited editGroupAvatar(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
        try {
            this.imageFacade.setGroupAvatar(id, new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
        return new GroupAvatarEdited();
    }

    @PutMapping("/banner/{id}")
    public GroupBannerEdited editGroupBanner(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
        try {
            this.imageFacade.setGroupBanner(id, new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
        return new GroupBannerEdited();
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getGroupAvatar(@PathVariable("id") UUID id) throws IOException {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getGroupAvatar(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
    }

    @GetMapping("/banner/{id}")
    public ResponseEntity<byte[]> getGroupBanner(@PathVariable("id") UUID id) throws IOException {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getGroupBanner(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
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
