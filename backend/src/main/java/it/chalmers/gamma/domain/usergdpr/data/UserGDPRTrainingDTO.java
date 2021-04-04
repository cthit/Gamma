package it.chalmers.gamma.domain.usergdpr.data;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;

public class UserGDPRTrainingDTO {

    @JsonUnwrapped
    private final UserRestrictedDTO user;
    private final boolean gdpr;

    public UserGDPRTrainingDTO(UserRestrictedDTO user, boolean gdpr) {
        this.user = user;
        this.gdpr = gdpr;
    }

    public UserRestrictedDTO getUser() {
        return user;
    }

    public boolean isGdpr() {
        return gdpr;
    }
}
