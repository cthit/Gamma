package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.MeFacade;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/internal/users/avatar")
public class UserAvatarController {

    //TODO: I think here that UserAvatarService would be nice
    private final MeFacade meFacade;

    public UserAvatarController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("id") UserId id) throws IOException {
//        ImageUri uri = userAvatarService.getUserAvatar(id).map(UserAvatar::avatarUri).orElse(new ImageUri("default_user_avatar.jpg"));
//        String[] uriData = uri.toString().split("\\.");
//        String type = uriData[uriData.length - 1];
//        byte[] data = imageService.getData(uri);
//        return ResponseEntity
//                .ok()
//                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
//                        : type.equals("png") ? MediaType.IMAGE_PNG
//                        : MediaType.IMAGE_GIF)
//                )
//                .body(data);
        return null;
    }

    @PutMapping
    public EditedProfilePictureResponse editProfileImage(@RequestParam MultipartFile file) {
//        try {
//            this.meService.editAvatar(file);
//        } catch (ImageService.ImageCouldNotBeSavedException | ImageService.ImageCouldNotBeRemovedException | ImageService.ImageContentNotValidException e) {
//            throw new FileIssueResponse();
//        }
        return new EditedProfilePictureResponse();
    }

    private static class EditedProfilePictureResponse extends SuccessResponse { }
    private static class UserNotFoundResponse extends NotFoundResponse { }

    private static class FileIssueResponse extends ErrorResponse {
        public FileIssueResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
