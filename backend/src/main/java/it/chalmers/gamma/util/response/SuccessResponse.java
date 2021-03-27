package it.chalmers.gamma.util.response;

import it.chalmers.gamma.util.ClassNameGeneratorUtils;
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
        return ClassNameGeneratorUtils.classToScreamingSnakeCase(this.getClass());
    }
}
