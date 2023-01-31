package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private static class GroupBannerEdited extends SuccessResponse {
    }

    private static class GroupAvatarEdited extends SuccessResponse {
    }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
