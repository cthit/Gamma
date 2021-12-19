package it.chalmers.gamma.adapter.primary.images;

import it.chalmers.gamma.app.image.ImageFacade;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImagesController {

    private final ImageFacade imageFacade;

    public ImagesController(ImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    @GetMapping("/user/avatar/{id}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("id") UUID id) {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getAvatar(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
    }

    @GetMapping("/group/avatar/{id}")
    public ResponseEntity<byte[]> getGroupAvatar(@PathVariable("id") UUID id) throws IOException {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getGroupAvatar(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType((type.equals("jpg") || type.equals("jpeg") ? MediaType.IMAGE_JPEG
                        : type.equals("png") ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
    }

    @GetMapping("/group/banner/{id}")
    public ResponseEntity<byte[]> getGroupBanner(@PathVariable("id") UUID id) throws IOException {
        ImageFacade.ImageDetails imageDetails = this.imageFacade.getGroupBanner(id);
        String type = imageDetails.imageType();
        return ResponseEntity
                .ok()
                .contentType(
                        (type.equals("jpg") || type.equals("jpeg")
                                ? MediaType.IMAGE_JPEG
                                : type.equals("png")
                                        ? MediaType.IMAGE_PNG
                                        : MediaType.IMAGE_GIF)
                )
                .body(imageDetails.data());
    }


}
