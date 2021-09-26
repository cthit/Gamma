package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.image.ImageService;
import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    public List<UserFacade.UserDTO> getAllRestrictedUsers() {
        return this.userFacade.getAll();
    }

    @GetMapping("/{id}")
    public UserFacade.UserExtendedWithGroupsDTO getUser(@PathVariable("id") UUID id) {
        return this.userFacade.getAsAdmin(id).orElseThrow();
    }

    record CreateUserRequest(String token,
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
    @ResponseBody
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
                ), request.token
        );
        return new UserCreatedResponse();
    }

    private static class UserCreatedResponse extends SuccessResponse { }

}
