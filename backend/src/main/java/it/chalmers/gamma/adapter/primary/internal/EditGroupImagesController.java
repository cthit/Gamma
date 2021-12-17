package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.facade.internal.ImageFacade;
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
public final class EditGroupImagesController {

    private final ImageFacade imageFacade;

    public EditGroupImagesController(ImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    //TODO: Force aspect ratio of avatar and banner

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

    private static class GroupBannerEdited extends SuccessResponse { }
    private static class GroupAvatarEdited extends SuccessResponse { }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
