package it.chalmers.gamma.usergdpr.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;
import it.chalmers.gamma.usergdpr.dto.UserWithGDPRDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UsersWithGDPRResponse {

    private final List<UserWithGDPRDTO> users;

    public UsersWithGDPRResponse(List<UserWithGDPRDTO> users) {
        this.users = users;
    }

    public List<UserWithGDPRDTO> getUsers() {
        return users;
    }

    public UsersWithGDPRResponseObject toResponseObject() {
        return new UsersWithGDPRResponseObject(this);
    }

    public static class UsersWithGDPRResponseObject extends ResponseEntity<UsersWithGDPRResponse> {
        public UsersWithGDPRResponseObject(UsersWithGDPRResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
