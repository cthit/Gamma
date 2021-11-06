package it.chalmers.gamma.adapter.primary.api.chalmersit;

import it.chalmers.gamma.app.facade.internal.GroupFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ChalmersitApiController.URI)
public class ChalmersitApiController {

    public static final String URI = "/external/chalmersit";

    private final GroupFacade groupFacade;

    public ChalmersitApiController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    // add /users/me

    // add get all groups that's a committee and SOCIETY
}
