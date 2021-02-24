package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.user.controller.response.GetAllUsersMinifiedResponse;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.util.Utils;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GetAllUsersMinifiedResponse> getAllUserMini() {
        return Utils.toResponseObject(new GetAllUsersMinifiedResponse(this.userFinder.getUsersRestricted()));
    }

}
