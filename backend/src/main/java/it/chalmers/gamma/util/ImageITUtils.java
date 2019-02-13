package it.chalmers.gamma.util;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class ImageITUtils {

    private static String imageITURL;
    private static String apiKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageITUtils.class);

    public static String saveImage(MultipartFile file) {
        File f = null;
        try {
            f = convertToFile(file);
            HttpPost post = new HttpPost(imageITURL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", f, ContentType.MULTIPART_FORM_DATA, f.getName());
            HttpEntity entity = builder.build();
            post.addHeader("API_KEY", apiKey);
            post.setEntity(entity);
            CloseableHttpClient client = HttpClients.custom().build();
            HttpResponse response = client.execute(post);
            HttpEntity res = response.getEntity();
            String returnUrl = imageITURL + EntityUtils.toString(res);
            LOGGER.info("saving file to " + returnUrl);
            return returnUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            f.delete();
        }
        return null;
    }

    private static File convertToFile(MultipartFile file) throws IOException {
        File f = new File(Objects.requireNonNull((file.getOriginalFilename())));
        if (f.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(file.getBytes());
            fos.close();
            return f;
        }
        return f;
    }

    /*
     * Needed for Spring to inject into static fields
     */
    @Value("${application.imageit.url}")
    public void setImageITURL(String imageITURL) {
        ImageITUtils.imageITURL = imageITURL;
    }
    
    @Value("${application.imageit.apiKey}")
    public void setApiKey(String apiKey) {
        ImageITUtils.apiKey = apiKey;
    }
}