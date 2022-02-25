package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;

import java.util.UUID;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/users")
public final class UserAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    private final UserFacade userFacade;
    private final UserCreationFacade userCreationFacade;

    public UserAdminController(UserFacade userFacade, UserCreationFacade userCreationFacade) {
        this.userFacade = userFacade;
        this.userCreationFacade = userCreationFacade;
    }

    record AdminChangePasswordRequest(String password) {}

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

    record AdminViewCreateUserRequest(String cid,
                                         String password,
                                         String nick,
                                         String firstName,
                                         String lastName,
                                         String email,
                                         int acceptanceYear,
                                         String language) { }

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

    record EditUserRequest (String nick,
                            String firstName,
                            String lastName,
                            String email,
                            String language,
                            int acceptanceYear) { }

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
                        request.language
                )
        );
        return new UserEditedResponse();
    }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class UserDeletedResponse extends SuccessResponse { }

    private static class UserCreatedResponse extends SuccessResponse { }

    private static class UserEditedResponse extends SuccessResponse { }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
