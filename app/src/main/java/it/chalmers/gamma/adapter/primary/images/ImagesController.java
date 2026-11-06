package it.chalmers.gamma.adapter.primary.images;

import it.chalmers.gamma.app.image.ImageFacade;
import java.time.Duration;
import java.util.UUID;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves avatars and banners.
 *
 * <p>Image urls rendered by Gamma include the version of the image ({@code ?v=}, see {@link
 * it.chalmers.gamma.app.image.domain.ImageUri#version()}). Since a new upload gives a new version,
 * a response for the current version never changes and can be cached for a long time. Requests
 * without a version, or with an outdated one, must be revalidated with the ETag so that a new
 * upload is picked up immediately.
 */
@RestController
@RequestMapping("/images")
public class ImagesController {

  private static final CacheControl VERSIONED_CACHE_CONTROL =
      CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable();
  private static final CacheControl UNVERSIONED_CACHE_CONTROL =
      CacheControl.noCache().cachePublic();

  private final ImageFacade imageFacade;

  public ImagesController(ImageFacade imageFacade) {
    this.imageFacade = imageFacade;
  }

  @GetMapping("/user/avatar/{id}")
  public ResponseEntity<byte[]> getUserAvatar(
      @PathVariable("id") UUID id, @RequestParam(value = "v", required = false) String version) {
    return versionedImage(this.imageFacade.getAvatar(id), version);
  }

  @GetMapping("/group/avatar/{id}")
  public ResponseEntity<byte[]> getGroupAvatar(
      @PathVariable("id") UUID id, @RequestParam(value = "v", required = false) String version) {
    return versionedImage(this.imageFacade.getGroupAvatar(id), version);
  }

  @GetMapping("/group/banner/{id}")
  public ResponseEntity<byte[]> getGroupBanner(
      @PathVariable("id") UUID id, @RequestParam(value = "v", required = false) String version) {
    return versionedImage(this.imageFacade.getGroupBanner(id), version);
  }

  /** Returns the avatar of a random group in the super group, so it must never be cached. */
  @GetMapping("/super-group/avatar/{id}")
  public ResponseEntity<byte[]> getSuperGroupAvatar(@PathVariable("id") UUID id) {
    return uncachedImage(this.imageFacade.getSuperGroupAvatar(id));
  }

  /** Returns the banner of a random group in the super group, so it must never be cached. */
  @GetMapping("/super-group/banner/{id}")
  public ResponseEntity<byte[]> getSuperGroupBanner(@PathVariable("id") UUID id) {
    return uncachedImage(this.imageFacade.getSuperGroupBanner(id));
  }

  private static ResponseEntity<byte[]> versionedImage(
      ImageFacade.ImageDetails image, String requestedVersion) {
    CacheControl cacheControl =
        image.version().equals(requestedVersion)
            ? VERSIONED_CACHE_CONTROL
            : UNVERSIONED_CACHE_CONTROL;

    // Spring answers 304 Not Modified when the request's If-None-Match matches the ETag.
    return ResponseEntity.ok()
        .contentType(mediaType(image))
        .cacheControl(cacheControl)
        .eTag(image.version())
        .body(image.data());
  }

  private static ResponseEntity<byte[]> uncachedImage(ImageFacade.ImageDetails image) {
    return ResponseEntity.ok()
        .contentType(mediaType(image))
        .cacheControl(CacheControl.noStore())
        .body(image.data());
  }

  private static MediaType mediaType(ImageFacade.ImageDetails image) {
    return switch (image.imageType()) {
      case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
      case "png" -> MediaType.IMAGE_PNG;
      default -> MediaType.IMAGE_GIF;
    };
  }
}
