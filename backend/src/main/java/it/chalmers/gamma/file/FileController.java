package it.chalmers.gamma.file;

import it.chalmers.gamma.file.response.FileNotFoundResponse;
import it.chalmers.gamma.file.response.FileTooLargeResponse;
import it.chalmers.gamma.file.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ControllerAdvice
@RestController
@RequestMapping()
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @GetMapping("/uploads/{id}.{type}")
    public GetFileResponse getFile(@PathVariable("id") String fileName, @PathVariable("type") String type) {
        String filePath = String.format("uploads/%s.%s", fileName, type);
        try {
            byte[] data = StreamUtils.copyToByteArray(Files.newInputStream(Paths.get(filePath)));
            return new GetFileResponse(data);
        } catch (IOException e) {
            throw new FileNotFoundResponse();
        }
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public FileTooLargeResponse handleUploadSizeException() {
        LOGGER.info("Too large file upload was attempted");
        return new FileTooLargeResponse();
    }

}
