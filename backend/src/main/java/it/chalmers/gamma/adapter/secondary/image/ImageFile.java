package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public record ImageFile(MultipartFile file) implements Image {

    public ImageFile {
        Objects.requireNonNull(file);
    }

}
