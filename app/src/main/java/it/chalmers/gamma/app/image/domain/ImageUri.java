package it.chalmers.gamma.app.image.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public record ImageUri(String value) implements Serializable {

  public ImageUri {
    if (value == null) {
      throw new NullPointerException("Image Uri cannot be null");
    } else if (!(value.endsWith(".png")
        || value.endsWith(".jpg")
        || value.endsWith(".gif")
        || value.endsWith(".jpeg"))) {
      throw new IllegalArgumentException("Image uri must end with .png, .jpg, .jpeg or .gif");
    }
  }

  /**
   * A short token that identifies this image. Every upload is stored under a new uri, so the token
   * changes whenever a new image is uploaded. It is used in image urls (as {@code ?v=}) and as the
   * ETag of image responses so that browsers and caches never serve a stale image.
   */
  public String version() {
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256").digest(this.value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash, 0, 8);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is always available", e);
    }
  }

  public static ImageUri defaultGroupBanner() {
    return new ImageUri("default_group_banner.jpg");
  }

  public static ImageUri defaultGroupAvatar() {
    return new ImageUri("default_group_avatar.jpg");
  }

  public static ImageUri defaultUserAvatar() {
    return new ImageUri("default_user_avatar.jpg");
  }
}
