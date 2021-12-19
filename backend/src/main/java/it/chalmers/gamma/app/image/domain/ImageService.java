package it.chalmers.gamma.app.image.domain;

import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageUri;

public interface ImageService {

    ImageUri saveImage(Image image) throws ImageCouldNotBeSavedException;
    void removeImage(ImageUri imageUri) throws ImageCouldNotBeRemovedException;

    record ImageDetails(byte[] data, String type) { }

    ImageDetails getImage(ImageUri imageUri);

    class ImageCouldNotBeRemovedException extends Exception { }

    class ImageCouldNotBeSavedException extends Exception {
        public ImageCouldNotBeSavedException(String message) {
            super(message);
        }

        public ImageCouldNotBeSavedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
