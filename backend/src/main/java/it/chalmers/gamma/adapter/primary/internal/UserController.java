package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public final class UserController {

    private final UserFacade userFacade;
    private final UserCreationFacade userCreationFacade;

    public UserController(UserFacade userFacade,
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

    record CreateUserRequest(String code,
                             String password,
                             String nick,
                             String firstName,
                             String email,
                             String lastName,
                             boolean userAgreement,
                             int acceptanceYear,
                             String cid,
                             String language) {}

    @PostMapping("/create")
    public UserCreatedResponse createUser(@RequestBody CreateUserRequest request) {
        //TODO: Check userAgreement

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
        return new UserCreatedResponse();
    }

    private static class UserCreatedResponse extends SuccessResponse { }

}
