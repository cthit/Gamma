package it.chalmers.gamma.response;

import com.sun.mail.iap.Response;
import it.chalmers.gamma.db.entity.ITUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UsersViewListResponse extends ResponseEntity<List<ITUser.ITUserView>> {
    public UsersViewListResponse(List<ITUser.ITUserView> views) {
        super(views, HttpStatus.ACCEPTED);
    }
}
