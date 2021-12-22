package it.chalmers.gamma.app;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.User;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class ListOfUserDTOAssert extends AbstractAssert<ListOfUserDTOAssert, List<UserFacade.UserDTO>> {

    protected ListOfUserDTOAssert(List<UserFacade.UserDTO> userDTOS) {
        super(userDTOS, ListOfUserDTOAssert.class);
    }

    public static ListOfUserDTOAssert assertThat(List<UserFacade.UserDTO> userDTOS) {
        return new ListOfUserDTOAssert(userDTOS);
    }

    public ListOfUserDTOAssert hasSameElements(List<User> users) {
        isNotNull();

        throw new UnsupportedOperationException();
    }

}
