package it.chalmers.gamma.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.response.FileNotSavedException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@SuppressFBWarnings(justification = "Needed for Spring to inject value, This is not in issue, FB is projecting",
        value = {"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);
    private static String relativePath;

    public static String saveImage(MultipartFile file) throws IOException {
        File f;
        try {
            f = saveToDisk(file);
            if(f != null) {
                return f.getPath();
            }
            throw new FileNotSavedException();
        }
        catch (IOException e ) {
            throw new IOException(e);
        }
    }

    private static File saveToDisk(MultipartFile file) throws IOException {
        File f = new File(relativePath + file.getOriginalFilename());
        File dir = new File(f.getParent());
        if (dir.mkdir()) {
            LOGGER.info("no uploads directory exists, creating a new one");
        }
        if (f.createNewFile()) {
            OutputStream fos = Files.newOutputStream(Path.of(f.getPath()));
            fos.write(file.getBytes());
            fos.close();
            return f;
        }
        return null;
    }

    /**
     * It's pronounced Gif
     *
     * @return true if file is image or gif, false otherwise
     */
    public static boolean isImageOrGif(MultipartFile file) {
        String contentType = file.getContentType();
        return List.of(
                ContentType.IMAGE_GIF.toString(),
                ContentType.IMAGE_PNG.toString(),
                ContentType.IMAGE_JPEG.toString())
                .contains(contentType);
    }

    // Needed to inject into static field
    @Value("${application.files.path}")
    public void setRelativePath(String relativePath) {
        ImageUtils.relativePath = relativePath;
    }
}