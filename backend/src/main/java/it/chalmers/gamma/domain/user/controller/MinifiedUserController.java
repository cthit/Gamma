package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.user.controller.response.GetAllUsersMinifiedResponse;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/minified")
public class MinifiedUserController {

    private final UserFinder userFinder;

    public MinifiedUserController(UserFinder userFinder) {
        this.userFinder = userFinder;
    }

    @GetMapping()
    public GetAllUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject getAllUserMini() {
        return new GetAllUsersMinifiedResponse(this.userFinder.getUsersRestricted()).toResponseObject();
    }


}
