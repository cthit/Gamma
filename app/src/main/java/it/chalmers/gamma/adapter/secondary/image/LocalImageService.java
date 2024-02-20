package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
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

  public LocalImageService(@Value("${application.files.path}") String relativePath) {
    this.relativePath = relativePath;
  }

  public ImageUri saveImage(Image image) throws ImageCouldNotBeSavedException {
    if (image instanceof ImageFile) {
      MultipartFile file = ((ImageFile) image).file();

      this.checkIfValidImageContent(file);

      String filePathString =
          UUID.randomUUID() + "/" + file.getName() + "." + getExtension(file.getOriginalFilename());

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

      LOGGER.info("Image " + file.getOriginalFilename() + " was uploaded.");

      return new ImageUri(filePathString);
    }
    throw new RuntimeException("Image not of type ImageFile");
  }

  public void removeImage(ImageUri imageUri) throws ImageCouldNotBeRemovedException {
    File f = new File(relativePath + imageUri.value());
    if (!f.delete()) {
      LOGGER.error("Could not delete the file: " + imageUri);
      throw new ImageCouldNotBeRemovedException();
    }
  }

  @Override
  public ImageDetails getImage(ImageUri imageUri) {
    try {
      return new ImageDetails(
          StreamUtils.copyToByteArray(
              Files.newInputStream(Paths.get(this.relativePath + imageUri.value()))),
          getType(imageUri));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private String getExtension(String fileName) {
    if (fileName == null) {
      throw new IllegalArgumentException();
    }

    return fileName.substring(fileName.lastIndexOf('.') + 1);
  }

  private String getType(ImageUri imageUri) {
    String[] split = imageUri.value().split("\\.");
    return split[split.length - 1];
  }

  private void checkIfValidImageContent(MultipartFile file) throws ImageCouldNotBeSavedException {
    String contentType = file.getContentType();
    if (!List.of("image/jpeg", "image/png", "image/gif").contains(contentType)) {
      throw new ImageCouldNotBeSavedException("Image content not valid");
    }
  }
}
