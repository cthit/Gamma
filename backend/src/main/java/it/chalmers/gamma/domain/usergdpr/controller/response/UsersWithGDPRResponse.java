package it.chalmers.gamma.domain.usergdpr.controller.response;

import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTrainingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UsersWithGDPRResponse {

    private final List<UserGDPRTrainingDTO> users;

    public UsersWithGDPRResponse(List<UserGDPRTrainingDTO> users) {
        this.users = users;
    }

    public List<UserGDPRTrainingDTO> getUsers() {
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
