package it.chalmers.gamma.response;

import com.sun.mail.iap.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PasswordChangedResponse extends ResponseEntity<String> {
    public PasswordChangedResponse() {
        super("PASSWORD_CHANGED",HttpStatus.ACCEPTED);
    }
}
