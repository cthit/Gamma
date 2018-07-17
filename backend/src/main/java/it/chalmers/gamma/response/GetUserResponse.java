package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ITUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserResponse extends ResponseEntity<ITUser>{
    public GetUserResponse(ITUser user) {
        super(user, HttpStatus.ACCEPTED);
    }
}
