package it.chalmers.gamma.adapter.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MiscBootstrap {

    @Value("classpath:/image/default_user_avatar.jpg")
    private Resource defaultResourceFile;

    @Value("${application.files.path}")
    private String targetDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(MiscBootstrap.class);

    public void runImageBootstrap() {
        File targetFile = new File(String.format("%s/%s", this.targetDir, "default_user_avatar.jpg"));
        if (!targetFile.exists()) {
            LOGGER.info("Default Avatar file does not exist, creating a new one");
            try {
                File defaultFile = this.defaultResourceFile.getFile();
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
                LOGGER.warn("Could not add a default avatar image");
            }
        }
    }
}
