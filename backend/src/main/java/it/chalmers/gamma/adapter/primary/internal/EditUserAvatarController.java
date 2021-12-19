package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/internal/users/avatar")
public class EditUserAvatarController {

    //TODO: I think here that UserAvatarService would be nice
    private final UserFacade userFacade;
    private final ImageFacade imageFacade;

    public EditUserAvatarController(UserFacade userFacade,
                                    ImageFacade imageFacade) {
        this.userFacade = userFacade;
        this.imageFacade = imageFacade;
    }

    //TODO: Force aspect ratio of avatar

    @PutMapping
    public EditedProfilePictureResponse editProfileImage(@RequestParam MultipartFile file) {
        try {
            this.imageFacade.setMeAvatar(new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new FileIssueResponse();
        }
        return new EditedProfilePictureResponse();
    }

    private static class EditedProfilePictureResponse extends SuccessResponse { }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
