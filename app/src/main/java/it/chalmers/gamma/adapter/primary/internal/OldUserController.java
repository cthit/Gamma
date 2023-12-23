package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
public final class OldUserController {

    private final UserFacade userFacade;
    private final UserCreationFacade userCreationFacade;

    public OldUserController(UserFacade userFacade,
                             UserCreationFacade userCreationFacade) {
        this.userFacade = userFacade;
        this.userCreationFacade = userCreationFacade;
    }

    @GetMapping()
    public List<UserFacade.UserDTO> getAllUsers() {
        return this.userFacade.getAll();
    }

    @GetMapping("/{id}")
    public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
        return this.userFacade.get(id).orElseThrow();
    }

    @PostMapping("/create")
    public UserCreatedResponse createUser(@RequestBody CreateUserRequest request) {
        if(!request.userAgreement) {
            throw new NotAcceptedUserAgreementResponse();
        }

        //TODO: Check for any exceptions, and throw a generic error if something goes wrong
        try {
            this.userCreationFacade.createUserWithCode(
                    new UserCreationFacade.NewUser(
                            request.password,
                            request.nick,
                            request.firstName,
                            request.email,
                            request.lastName,
                            request.acceptanceYear,
                            request.cid,
                            request.language
                    ), request.code
            );
        } catch (UserCreationFacade.SomePropertyNotUniqueException e) {
            e.printStackTrace();
        }
        return new UserCreatedResponse();
    }

    record CreateUserRequest(String code,
                             String password,
                             String nick,
                             String firstName,
                             String email,
                             String lastName,
                             boolean userAgreement,
                             int acceptanceYear,
                             String cid,
                             String language) {
    }

    private static class UserCreatedResponse extends SuccessResponse {
    }

    private static class NotAcceptedUserAgreementResponse extends ErrorResponse {

        public NotAcceptedUserAgreementResponse() {
            super(HttpStatus.BAD_REQUEST);
        }
    }

}
