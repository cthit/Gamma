package it.chalmers.gamma.util.response;

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
                this.getStatusCodeValue()
        );
    }

    public static class SuccessResponseData {

        public final String name;
        public final int code;

        public SuccessResponseData(String name, int code) {
            this.name = name;
            this.code = code;
        }
    }

}
