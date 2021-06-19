package it.chalmers.gamma.internal.useravatar.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.ImageUri;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserAvatar;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.internal.useravatar.service.UserAvatarService;
import it.chalmers.gamma.util.component.ImageService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
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
import java.security.Principal;

@RestController
@RequestMapping("/internal/users/avatar")
public class UserAvatarController {

    private final UserService userService;
    private final UserAvatarService userAvatarService;
    private final ImageService imageService;

    public UserAvatarController(UserService userService,
                                UserAvatarService userAvatarService,
                                ImageService imageService) {
        this.userService = userService;
        this.userAvatarService = userAvatarService;
        this.imageService = imageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("id") UserId id) throws IOException {
        ImageUri uri = userAvatarService.getUserAvatar(id).map(UserAvatar::avatarUri).orElse(ImageUri.valueOf("default_user_avatar.jpg"));
        String[] uriData = uri.toString().split("\\.");
        String type = uriData[uriData.length - 1];
        byte[] data = imageService.getData(uri);
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(data);
    }

    @PutMapping
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        try {
            User user = userService.get(Cid.valueOf(principal.getName()));
            this.userAvatarService.editUserAvatar(user.id(), file);
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        } catch (ImageService.FileCouldNotBeSavedException | ImageService.FileCouldNotBeRemovedException | ImageService.FileContentNotValidException e) {
            throw new FileIssueResponse();
        }
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
