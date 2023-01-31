package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.image.domain.Image;
import org.apache.http.entity.ContentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

public record ImageFile(MultipartFile file) implements Image {

    public ImageFile {
        Objects.requireNonNull(file);
        if (!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType(), ContentType.IMAGE_GIF.getMimeType())
                .contains(file.getContentType())) {
            throw new IllegalStateException("File must be an Image");
        }
    }

}
