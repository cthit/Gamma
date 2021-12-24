package it.chalmers.gamma.app;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.User;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class UserDTOAssert extends AbstractAssert<UserDTOAssert, UserFacade.UserDTO> {

    protected UserDTOAssert(UserFacade.UserDTO userDTO) {
        super(userDTO, UserDTOAssert.class);
    }

    public static UserDTOAssert assertThat(UserFacade.UserDTO userDTO) {
        return new UserDTOAssert(userDTO);
    }

    public UserDTOAssert isEqualTo(User user) {
        isNotNull();

        Assertions.assertThat(actual)
                .hasOnlyFields("cid", "nick", "firstName", "lastName", "id", "acceptanceYear")
                .isEqualTo(new UserFacade.UserDTO(
                        user.cid().value(),
                        user.nick().value(),
                        user.firstName().value(),
                        user.lastName().value(),
                        user.id().value(),
                        user.acceptanceYear().value()
                ));

        return this;
    }

}
