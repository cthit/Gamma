package it.chalmers.gamma.adapter.secondary.image;

import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImageRemovalJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(ImageRemovalJob.class);
  private final JdbcTemplate jdbc;
  private final ImageService images;
  private final Path directory;

  public ImageRemovalJob(
      JdbcTemplate jdbc,
      ImageService images,
      @Value("${application.files.path}") String directory) {
    this.jdbc = jdbc;
    this.images = images;
    this.directory = Path.of(directory).toAbsolutePath().normalize();
  }

  @Scheduled(fixedDelay = 10000, initialDelay = 1000)
  @Transactional
  public void removeQueuedImages() {
    // Lock a bounded batch so multiple replicas safely share the queue. Missing files
    // count as success, including a restart after unlink but before this transaction commits.
    for (String uri :
        jdbc.queryForList(
            "SELECT image_uri FROM g_image_removal WHERE retry_after <= CURRENT_TIMESTAMP ORDER BY retry_after LIMIT 100 FOR UPDATE SKIP LOCKED",
            String.class)) {
      if (isReferenced(uri)) {
        jdbc.update("DELETE FROM g_image_removal WHERE image_uri = ?", uri);
        continue;
      }
      try {
        images.removeImage(new ImageUri(uri));
        jdbc.update("DELETE FROM g_image_removal WHERE image_uri = ?", uri);
      } catch (ImageService.ImageCouldNotBeRemovedException e) {
        jdbc.update(
            "UPDATE g_image_removal SET retry_after = CURRENT_TIMESTAMP + INTERVAL '5 minutes' WHERE image_uri = ?",
            uri);
        LOGGER.warn("An image could not be removed; it remains queued for retry");
      }
    }
  }

  @Scheduled(fixedDelay = 3600000, initialDelay = 60000)
  public void queueLegacyOrphanImages() {
    // Also discover files left by older releases or a failed upload transaction. A one-day
    // grace period protects newly uploaded files whose database transaction is still pending.
    if (!Files.isDirectory(directory)) {
      return;
    }
    Instant before = Instant.now().minus(1, ChronoUnit.DAYS);
    try (var files = Files.walk(directory, 2)) {
      files
          .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
          .filter(path -> {
            Path relative = directory.relativize(path);
            if (relative.getNameCount() != 2) {
              return false;
            }
            try {
              java.util.UUID.fromString(relative.getName(0).toString());
              return true;
            } catch (IllegalArgumentException e) {
              return false;
            }
          })
          .filter(path -> {
            try {
              return Files.getLastModifiedTime(path).toInstant().isBefore(before);
            } catch (IOException e) {
              return false;
            }
          })
          .forEach(path -> {
            String uri = directory.relativize(path).toString();
            if (!isReferenced(uri)) {
              jdbc.update(
                  "INSERT INTO g_image_removal (image_uri) VALUES (?) ON CONFLICT DO NOTHING", uri);
            }
          });
    } catch (IOException e) {
      LOGGER.warn("Could not inspect orphan images; the next sweep will retry", e);
    }
  }

  private boolean isReferenced(String uri) {
    return Boolean.TRUE.equals(
        jdbc.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM g_user_avatar_uri WHERE avatar_uri = ? UNION ALL SELECT 1 FROM g_group_images_uri WHERE avatar_uri = ? OR banner_uri = ?)",
            Boolean.class,
            uri,
            uri,
            uri));
  }
}
