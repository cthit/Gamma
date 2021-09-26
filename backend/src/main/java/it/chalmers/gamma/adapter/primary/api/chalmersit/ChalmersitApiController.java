package it.chalmers.gamma.adapter.primary.api.chalmersit;

import it.chalmers.gamma.app.group.GroupFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ChalmersitApiController.API)
public class ChalmersitApiController {

    public static final String API = "/chalmersit";

    private final GroupFacade groupFacade;

    public ChalmersitApiController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    // add /users/me

    // add get all groups that's a committee and SOCIETY
}
