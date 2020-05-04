package it.chalmers.gamma.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseStatusException extends ResponseStatusException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomResponseStatusException.class);

    public CustomResponseStatusException(HttpStatus status, String reason) {
        super(status, reason);
        LOGGER.error(String.format(
                "An exception was thrown in the application: \n status: %d, \n Reason: %s",
                status.value(),
                reason));
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
