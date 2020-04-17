package it.chalmers.gamma.controller;

import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.GetFileResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
public class FileController {
    @GetMapping("/{id}.{type}")
    public GetFileResponse getFile(@PathVariable("id") String fileName, @PathVariable("type") String type) {
        File imageFile = new File(String.format("uploads/%s.%s", fileName, type));
        if (!imageFile.isFile()) {
            throw new FileNotFoundResponse();
        }
        try {
            byte[] data = StreamUtils.copyToByteArray(new FileInputStream(imageFile));
            return new GetFileResponse(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileNotFoundResponse();
    }
}
