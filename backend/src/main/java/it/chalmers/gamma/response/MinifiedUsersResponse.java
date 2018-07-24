package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ITUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MinifiedUsersResponse extends ResponseEntity<List<ITUser.ITUserView>>{

    public MinifiedUsersResponse(List<ITUser.ITUserView> itUser) {
        super(itUser, HttpStatus.ACCEPTED);
    }
}
