package it.chalmers.gamma.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
        File targetFile = new File(String.format("%s/%s", this.targetDir, resource.getFilename()));
        if (!targetFile.exists()) {
            LOGGER.info("default file: " + resource.getFilename() + " does not exist in " + this.targetDir + ", creating a new one");
            try {
                File defaultFile = resource.getFile();
                if (defaultFile.isFile()) {
                    File targetDirFile = new File(this.targetDir);
                    if (!targetDirFile.mkdir()) {
                        LOGGER.warn("Could not create target directory");
                    }
                    Files.copy(defaultFile.toPath(), targetFile.toPath());
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                LOGGER.warn("Could not copy: " + resource.getFilename());
                LOGGER.error(e.getMessage());
            }
        }
    }
}
