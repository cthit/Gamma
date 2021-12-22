package it.chalmers.gamma.app;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.User;
import org.assertj.core.api.AbstractAssert;

public class UserDTOAssert extends AbstractAssert<UserDTOAssert, UserFacade.UserDTO> {

    protected UserDTOAssert(UserFacade.UserDTO userDTO) {
        super(userDTO, UserDTOAssert.class);
    }

    public static UserDTOAssert assertThat(UserFacade.UserDTO userDTO) {
        return new UserDTOAssert(userDTO);
    }

    public UserDTOAssert isEqualTo(User user) {
        isNotNull();

        throw new UnsupportedOperationException();
    }

}
