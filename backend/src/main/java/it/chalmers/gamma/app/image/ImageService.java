package it.chalmers.gamma.app.image;

import it.chalmers.gamma.domain.common.ImageUri;

public interface ImageService {

    ImageUri saveImage(Image image) throws ImageCouldNotBeSavedException;
    void removeImage(ImageUri imageUri) throws ImageCouldNotBeRemovedException;
    String toUrl(ImageUri imageUri);

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
