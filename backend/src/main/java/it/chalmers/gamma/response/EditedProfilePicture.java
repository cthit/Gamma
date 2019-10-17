package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedProfilePicture extends ResponseEntity<String> {
    public EditedProfilePicture() {
        super("EDITED_PROFILE_PICTURE", HttpStatus.ACCEPTED);
    }
}
