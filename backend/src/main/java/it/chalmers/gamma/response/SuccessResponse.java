package it.chalmers.gamma.response;

import it.chalmers.gamma.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponse extends ResponseEntity<String> {

    public SuccessResponse() {
        super(HttpStatus.OK);
    }

    public SuccessResponse(HttpStatus status) {
        super(status);
    }

    @Override
    public String getBody() {
        return Utils.classToScreamingSnakeCase(this.getClass());
    }
}
