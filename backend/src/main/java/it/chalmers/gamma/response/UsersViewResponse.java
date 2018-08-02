package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ITUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UsersViewResponse extends ResponseEntity<ITUser.ITUserView> {
    public UsersViewResponse(ITUser.ITUserView view) {
        super(view, HttpStatus.ACCEPTED);
    }
}
