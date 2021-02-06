package it.chalmers.gamma.usergdpr.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;

public class UserWithGDPRDTO {

    @JsonUnwrapped
    private UserRestrictedDTO user;
    private boolean gdpr;

    public UserWithGDPRDTO(UserRestrictedDTO user, boolean gdpr) {
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
