package it.chalmers.gamma.adapter.primary.api.v1;

import it.chalmers.gamma.app.facade.GroupFacade;
import it.chalmers.gamma.app.facade.SuperGroupFacade;
import it.chalmers.gamma.app.facade.MeFacade;
import it.chalmers.gamma.app.facade.UserFacade;
import org.springframework.stereotype.Component;

@Component
public class ClientApiV1Mapper {

    public ClientApiV1Controller.Group map(GroupFacade.GroupDTO group) {
        return new ClientApiV1Controller.Group();
    }

    public ClientApiV1Controller.Me map(MeFacade.MeDTO me) {
        return new ClientApiV1Controller.Me();
    }

    public ClientApiV1Controller.SuperGroup map(SuperGroupFacade.SuperGroupDTO superGroup) {
        return new ClientApiV1Controller.SuperGroup();
    }

    public ClientApiV1Controller.User map(UserFacade.UserDTO user) {
        return new ClientApiV1Controller.User();
    }

}
