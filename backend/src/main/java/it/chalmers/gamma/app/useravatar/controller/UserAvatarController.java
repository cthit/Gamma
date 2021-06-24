package it.chalmers.gamma.app.useravatar.controller;

import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.app.domain.UserAvatar;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.user.service.MeService;
import it.chalmers.gamma.app.user.service.UserService;
import it.chalmers.gamma.app.useravatar.service.UserAvatarService;
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

@RestController
@RequestMapping("/internal/users/avatar")
public class UserAvatarController {

    private final UserService userService;
    private final UserAvatarService userAvatarService;
    private final ImageService imageService;
    private final MeService meService;

    public UserAvatarController(UserService userService,
                                UserAvatarService userAvatarService,
                                ImageService imageService,
                                MeService meService) {
        this.userService = userService;
        this.userAvatarService = userAvatarService;
        this.imageService = imageService;
        this.meService = meService;
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
    public EditedProfilePictureResponse editProfileImage(@RequestParam MultipartFile file) {
        try {
            this.meService.editAvatar(file);
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
