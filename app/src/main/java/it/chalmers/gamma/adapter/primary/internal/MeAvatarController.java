package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/internal/users/me/avatar")
public class MeAvatarController {

    private final MeFacade meFacade;

    public MeAvatarController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @PutMapping
    public EditedProfilePictureResponse editProfileImage(@RequestParam MultipartFile file) {
        try {
            this.meFacade.setAvatar(new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
        return new EditedProfilePictureResponse();
    }

    @DeleteMapping
    public DeletedProfilePictureResponse deleteProfileImage() {
        try {
            this.meFacade.deleteAvatar();
        } catch (ImageService.ImageCouldNotBeRemovedException e) {
            throw new RuntimeException(e);
        }
        return new DeletedProfilePictureResponse();
    }

    private static class DeletedProfilePictureResponse extends SuccessResponse {
    }

    private static class EditedProfilePictureResponse extends SuccessResponse {
    }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
