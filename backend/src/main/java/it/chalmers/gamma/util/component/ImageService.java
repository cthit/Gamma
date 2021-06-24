package it.chalmers.gamma.util.component;

import it.chalmers.gamma.app.domain.ImageUri;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;


@Component
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final String relativePath;
    private final String absoluteBasePath;

    public ImageService(@Value("${application.files.path}") String relativePath,
                        @Value("${application.base-uri}") String absoluteBasePath) {
        this.relativePath = relativePath;
        this.absoluteBasePath = absoluteBasePath;
    }

    public ImageUri saveImage(MultipartFile file) throws FileCouldNotBeSavedException, FileContentNotValidException {
        this.checkIfValidImageContent(file);

        String filePathString = UUID.randomUUID()
                + "/"
                + file.getName()
                + "."
                + FilenameUtils.getExtension(file.getOriginalFilename());

        File filePath = new File(this.relativePath + filePathString);

        File fileFolderPath = new File(filePath.getParent());
        if (!fileFolderPath.mkdirs()) {
            throw new FileCouldNotBeSavedException("File folder could not be created");
        }

        try {
            if (!filePath.createNewFile()) {
                throw new FileCouldNotBeSavedException("(1) File could not be created");
            }

            try (OutputStream fos = Files.newOutputStream(Path.of(filePath.getPath()))) {
                fos.write(file.getBytes());
            }

        } catch (IOException e) {
            throw new FileCouldNotBeSavedException("(2) File could not be created", e);
        }

        return ImageUri.valueOf(filePathString);
    }

    public void removeImage(ImageUri imageUri) throws FileCouldNotBeRemovedException {
        File f = new File(relativePath + imageUri);
        if (!f.delete()) {
            throw new FileCouldNotBeRemovedException();
        }
    }

    public String getAbsoluteImageUri(ImageUri imageUri) {
        return this.absoluteBasePath + this.relativePath + imageUri;
    }

    public byte[] getData(ImageUri uri) throws IOException {
        return StreamUtils.copyToByteArray(Files.newInputStream(Paths.get(this.relativePath + uri)));
    }

    private void checkIfValidImageContent(MultipartFile file) throws FileContentNotValidException {
        String contentType = file.getContentType();
        if (!List.of(
                ContentType.IMAGE_GIF.toString(),
                ContentType.IMAGE_PNG.toString(),
                ContentType.IMAGE_JPEG.toString()
        ).contains(contentType)) {
            throw new FileContentNotValidException();
        }
    }

    public static class FileContentNotValidException extends Exception { }

    public static class FileCouldNotBeRemovedException extends Exception { }

    public static class FileCouldNotBeSavedException extends Exception {
        public FileCouldNotBeSavedException(String message) {
            super(message);
        }

        public FileCouldNotBeSavedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}