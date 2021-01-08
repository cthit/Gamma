package it.chalmers.gamma.client.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientEditedResponse extends ResponseEntity<String> {

    public ClientEditedResponse() {
        super("EDITED_CLIENT_RESPONSE", HttpStatus.ACCEPTED);
    }
}
