package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.port.service.ImageService;
import it.chalmers.gamma.app.domain.common.ImageUri;

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
public class LocalImageService implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalImageService.class);

    private final String relativePath;
    private final String absoluteBasePath;

    public LocalImageService(@Value("${application.files.path}") String relativePath,
                             @Value("${application.base-uri}") String absoluteBasePath) {
        this.relativePath = relativePath;
        this.absoluteBasePath = absoluteBasePath;
    }

    public ImageUri saveImage(Image image) throws ImageCouldNotBeSavedException {
        if (image instanceof ImageFile) {
            MultipartFile file = ((ImageFile) image).file();

            this.checkIfValidImageContent(file);

            String filePathString = UUID.randomUUID()
                    + "/"
                    + file.getName()
                    + "."
                    + FilenameUtils.getExtension(file.getOriginalFilename());

            File filePath = new File(this.relativePath + filePathString);

            File fileFolderPath = new File(filePath.getParent());
            if (!fileFolderPath.mkdirs()) {
                throw new ImageCouldNotBeSavedException("File folder could not be created");
            }

            try {
                if (!filePath.createNewFile()) {
                    throw new ImageCouldNotBeSavedException("(1) File could not be created");
                }

                try (OutputStream fos = Files.newOutputStream(Path.of(filePath.getPath()))) {
                    fos.write(file.getBytes());
                }

            } catch (IOException e) {
                throw new ImageCouldNotBeSavedException("(2) File could not be created", e);
            }

            return new ImageUri(filePathString);
        }
        throw new RuntimeException("Image not of type ImageFile");
    }

    public void removeImage(ImageUri imageUri) throws ImageCouldNotBeRemovedException {
        File f = new File(relativePath + imageUri);
        if (!f.delete()) {
            throw new ImageCouldNotBeRemovedException();
        }
    }

    public String toUrl(ImageUri imageUri) {
        return this.absoluteBasePath + this.relativePath + imageUri;
    }

    public byte[] getData(ImageUri uri) throws IOException {
        return StreamUtils.copyToByteArray(Files.newInputStream(Paths.get(this.relativePath + uri)));
    }

    private void checkIfValidImageContent(MultipartFile file) throws ImageCouldNotBeSavedException {
        String contentType = file.getContentType();
        if (!List.of(
                ContentType.IMAGE_GIF.toString(),
                ContentType.IMAGE_PNG.toString(),
                ContentType.IMAGE_JPEG.toString()
        ).contains(contentType)) {
            throw new ImageCouldNotBeSavedException("Image content not valid");
        }
    }

}