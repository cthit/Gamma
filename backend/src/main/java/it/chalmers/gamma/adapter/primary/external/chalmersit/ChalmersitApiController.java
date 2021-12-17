package it.chalmers.gamma.adapter.primary.external.chalmersit;

import it.chalmers.gamma.app.facade.internal.GroupFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    record GroupsResponse(List<ChalmersitGroupDTO> groups) {
    }
    record ChalmersitGroupDTO(String name) {

    }

    @GetMapping("/groups")
    GroupsResponse getGroups() {
        return new GroupsResponse(
                new ArrayList<>(){{
                        add(new ChalmersitGroupDTO("hello"));
                }}
        );
    }

}
