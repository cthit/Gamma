package it.chalmers.gamma.adapter.primary.external.v1;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Component;

@Component
public class ClientApiV1Mapper {

    public ClientApiV1Controller.Group map(GroupFacade.GroupDTO group) {
        return new ClientApiV1Controller.Group();
    }

    public ClientApiV1Controller.Me map(MeFacade.MeDTO me) {
        return new ClientApiV1Controller.Me(me.nick());
    }

    public ClientApiV1Controller.SuperGroup map(SuperGroupFacade.SuperGroupDTO superGroup) {
        return new ClientApiV1Controller.SuperGroup();
    }

    public ClientApiV1Controller.User map(UserFacade.UserDTO user) {
        return new ClientApiV1Controller.User();
    }

}
