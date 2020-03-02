package it.chalmers.gamma.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@SuppressFBWarnings(justification = "Needed for Spring to inject value, This is not in issue, FB is projecting",
        value = {"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImageITUtils {

    private static String imageITUrl;
    private static String apiKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageITUtils.class);
    private static final int ACCEPTED_CODE = 200;

    public static String saveImage(MultipartFile file) throws IOException {
        File f;
        try {
            f = convertToFile(file);
            if (f == null) {
                throw new FileNotFoundException();
            }
            if (ImageIO.read(f) == null) {
                LOGGER.warn("upload of non-image file was attempted");
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new FileNotFoundException();
        }
        try {
            HttpPost post = new HttpPost(imageITUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", f, ContentType.MULTIPART_FORM_DATA, f.getName());
            HttpEntity entity = builder.build();
            post.addHeader("API_KEY", apiKey);
            post.setEntity(entity);
            CloseableHttpClient client = HttpClients.custom().build();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != ACCEPTED_CODE) {
                LOGGER.error("error connecting to ImageIT");
                throw new IOException();
            }
            HttpEntity res = response.getEntity();
            String returnUrl = imageITUrl + EntityUtils.toString(res);
            LOGGER.info("saving file to " + returnUrl);
            return returnUrl;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (f.delete()) {
                LOGGER.info("deleted local file");
            }
        }
        throw new FileNotFoundException();
    }

    private static File convertToFile(MultipartFile file) throws IOException {
        File f = new File(Objects.requireNonNull(Objects.requireNonNull(file).getOriginalFilename()));

        if (f.createNewFile()) {
            OutputStream fos = Files.newOutputStream(Paths.get(f.getPath()));
            fos.write(file.getBytes());
            fos.close();
            return f;
        }
        return null;
    }

    /*
     * Needed for Spring to inject into static fields
     */
    @Value("${application.imageit.url}")
    public void setImageITUrl(String imageITUrl) {
        ImageITUtils.imageITUrl = imageITUrl;
    }
    
    @Value("${application.imageit.apiKey}")
    public void setApiKey(String apiKey) {
        ImageITUtils.apiKey = apiKey;
    }
}