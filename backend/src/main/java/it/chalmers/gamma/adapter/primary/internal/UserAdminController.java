package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/users")
public final class UserAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    private final UserFacade userFacade;
    private final UserCreationFacade userCreationFacade;
    private final ImageFacade imageFacade;

    public UserAdminController(UserFacade userFacade,
                               UserCreationFacade userCreationFacade,
                               ImageFacade imageFacade) {
        this.userFacade = userFacade;
        this.userCreationFacade = userCreationFacade;
        this.imageFacade = imageFacade;
    }

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") UUID id,
            @RequestBody AdminChangePasswordRequest request) {
        this.userFacade.setUserPassword(id, request.password);
        return new PasswordChangedResponse();
    }

    @DeleteMapping("/{id}")
    public UserDeletedResponse deleteUser(@PathVariable("id") UUID id) {
        this.userFacade.deleteUser(id);
        return new UserDeletedResponse();
    }

    @GetMapping("/{id}")
    public UserFacade.UserExtendedWithGroupsDTO getUser(@PathVariable("id") UUID id) {
        return this.userFacade.getAsAdmin(id)
                .orElseThrow();
    }

    @PostMapping()
    public UserCreatedResponse addUser(@RequestBody AdminViewCreateUserRequest request) {
        try {
            this.userCreationFacade.createUser(
                    new UserCreationFacade.NewUser(
                            request.password,
                            request.nick,
                            request.firstName,
                            request.email,
                            request.lastName,
                            request.acceptanceYear,
                            request.cid,
                            request.language
                    )
            );
        } catch (UserCreationFacade.SomePropertyNotUniqueException e) {
            e.printStackTrace();
        }
        return new UserCreatedResponse();
    }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") UUID id,
                                       @RequestBody EditUserRequest request) {
        this.userFacade.updateUser(
                new UserFacade.UpdateUser(
                        id,
                        request.nick,
                        request.firstName,
                        request.lastName,
                        request.email,
                        request.language,
                        request.acceptanceYear
                )
        );
        return new UserEditedResponse();
    }

    @DeleteMapping("/{id}/remove-avatar")
    public UserAvatarRemovedResponse removeUserAvatar(@PathVariable("id") UUID userId) {
        this.imageFacade.removeUserAvatar(userId);
        return new UserAvatarRemovedResponse();
    }

    record AdminChangePasswordRequest(String password) {
    }

    record AdminViewCreateUserRequest(String cid,
                                      String password,
                                      String nick,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      int acceptanceYear,
                                      String language) {
    }

    record EditUserRequest(String nick,
                           String firstName,
                           String lastName,
                           String email,
                           String language,
                           int acceptanceYear) {
    }

    private static class PasswordChangedResponse extends SuccessResponse {
    }

    private static class UserDeletedResponse extends SuccessResponse {
    }

    private static class UserCreatedResponse extends SuccessResponse {
    }

    private static class UserEditedResponse extends SuccessResponse {
    }

    private static class UserAvatarRemovedResponse extends SuccessResponse {
    }

    private static class UserNotFoundResponse extends NotFoundResponse {
    }

}
