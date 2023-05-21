package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.image.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

public record ImageFile(MultipartFile file) implements Image {

    public ImageFile {
        Objects.requireNonNull(file);
        if (!Arrays.asList("image/jpeg", "image/png", "image/gif")
                .contains(file.getContentType())) {
            throw new IllegalStateException("File must be an Image");
        }
    }

}
