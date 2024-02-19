package it.chalmers.gamma.adapter.primary.api.utils;

import it.chalmers.gamma.util.ClassNameGeneratorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponse extends ResponseEntity<SuccessResponse.SuccessResponseData> {

    public SuccessResponse() {
        super(HttpStatus.OK);
    }

    public SuccessResponse(HttpStatus status) {
        super(status);
    }

    @Override
    public SuccessResponseData getBody() {
        return new SuccessResponseData(
                ClassNameGeneratorUtils.classToScreamingSnakeCase(this.getClass()),
                this.getStatusCode().value()
        );
    }

    public record SuccessResponseData(String name, int code) { }

}
