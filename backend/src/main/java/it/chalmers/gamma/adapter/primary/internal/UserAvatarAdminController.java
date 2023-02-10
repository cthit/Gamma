package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/users/avatar")
public class UserAvatarAdminController {

    private final ImageFacade imageFacade;

    public UserAvatarAdminController(ImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    @DeleteMapping("/{userId}")
    public UserAvatarDeletedResponse deleteUserAvatar(@PathVariable("userId") UUID userId) {
        this.imageFacade.removeUserAvatar(userId);
        return new UserAvatarDeletedResponse();
    }

    public static class UserAvatarDeletedResponse extends SuccessResponse {
    }

}
