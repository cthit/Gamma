package it.chalmers.gamma.handlers;

import it.chalmers.gamma.response.FileTooLargeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@RestController
public class MultipartExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartExceptionHandler.class);


    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public FileTooLargeResponse handleUploadSizeException() {
        LOGGER.info("Too large file upload was attempted");
        return new FileTooLargeResponse();
    }
}
