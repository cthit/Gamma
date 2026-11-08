package it.chalmers.gamma.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MiscBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(MiscBootstrap.class);

  @Value("classpath:/image/default_user_avatar.jpg")
  private Resource defaultUserAvatar;

  @Value("classpath:/image/default_group_banner.jpg")
  private Resource defaultGroupBanner;

  @Value("classpath:/image/default_group_avatar.jpg")
  private Resource defaultGroupAvatar;

  @Value("${application.files.path}")
  private String targetDir;

  public void runImageBootstrap() {
    assureFileExistsInUpload(defaultUserAvatar);
    assureFileExistsInUpload(defaultGroupAvatar);
    assureFileExistsInUpload(defaultGroupBanner);
  }

  private void assureFileExistsInUpload(Resource resource) {
    Path targetDirPath = Path.of(this.targetDir);
    Path targetFile = targetDirPath.resolve(resource.getFilename());
    if (!Files.exists(targetFile)) {
      LOGGER.info(
          "default file: "
              + resource.getFilename()
              + " does not exist in "
              + this.targetDir
              + ", creating a new one");
      try {
        Files.createDirectories(targetDirPath);
        // Read through the stream: getFile() fails when the resource is inside a packaged jar.
        try (InputStream in = resource.getInputStream()) {
          Files.copy(in, targetFile);
        }
      } catch (IOException e) {
        LOGGER.warn("Could not copy: " + resource.getFilename(), e);
      }
    }
  }
}
