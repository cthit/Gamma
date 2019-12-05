package it.chalmers.gamma.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedProfilePictureResponse extends ResponseEntity<String> {
    public EditedProfilePictureResponse() {
        super("EDITED_PROFILE_PICTURE", HttpStatus.ACCEPTED);
    }
}
