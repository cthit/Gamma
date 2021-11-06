package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.facade.internal.ImageFacade;
import it.chalmers.gamma.app.facade.internal.UserFacade;
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

import java.util.UUID;

@RestController
@RequestMapping("/internal/users/avatar")
public class UserAvatarController {

    //TODO: I think here that UserAvatarService would be nice
    private final UserFacade userFacade;
    private final ImageFacade imageFacade;

    public UserAvatarController(UserFacade userFacade,
                                ImageFacade imageFacade) {
        this.userFacade = userFacade;
        this.imageFacade = imageFacade;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("id") UUID id) {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getAvatar(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
    }

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
