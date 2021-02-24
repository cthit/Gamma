package it.chalmers.gamma.response;

import java.util.Arrays;
import java.util.stream.Collectors;

import it.chalmers.gamma.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ErrorResponse extends ResponseStatusException {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponse.class);

    public ErrorResponse(HttpStatus status) {
        super(status);

        LOGGER.error(String.format(
                "An exception was thrown in the application: \n status: %d, \n Reason: %s",
                status.value(),
                Utils.classToScreamingSnakeCase(this.getClass())));
        LOGGER.debug(String.format("Stacktrace: \n %s:", Arrays.stream(super.fillInStackTrace().getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.joining("\n    "))));
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    @Override
    public String getReason() {
        return Utils.classToScreamingSnakeCase(this.getClass());
    }

}
