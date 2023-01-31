package it.chalmers.gamma.adapter.primary.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@RestController
@RequestMapping("/uploads")
public final class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public FileTooLargeResponse handleUploadSizeException() {
        LOGGER.info("Too large file upload was attempted");
        return new FileTooLargeResponse();
    }

    public static class FileTooLargeResponse extends ResponseEntity<String> {
        public FileTooLargeResponse() {
            super("FILE_UPLOAD_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }


}
