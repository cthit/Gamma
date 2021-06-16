package it.chalmers.gamma.file;

import it.chalmers.gamma.file.response.FileNotFoundResponse;
import it.chalmers.gamma.file.response.FileTooLargeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ControllerAdvice
@RestController
@RequestMapping("/uploads")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public FileTooLargeResponse handleUploadSizeException() {
        LOGGER.info("Too large file upload was attempted");
        return new FileTooLargeResponse();
    }

}
