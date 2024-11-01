package it.chalmers.gamma.adapter.secondary.image;

import static it.chalmers.gamma.app.validation.ValidationHelper.result;

import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public record ImageFile(MultipartFile file) implements Image {

  public ImageFile {
    Objects.requireNonNull(file);
    new ImageFileValidator().validate(file);
  }

  public static final class ImageFileValidator implements Validator<MultipartFile> {

    @Override
    public ValidationResult validate(MultipartFile file) {
      return result(
          Arrays.asList("image/jpeg", "image/png", "image/gif").contains(file.getContentType()),
          "Must be an image");
    }
  }
}
