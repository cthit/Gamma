package it.chalmers.gamma.app.port.service;

import it.chalmers.gamma.app.domain.common.ImageUri;

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

    interface Image { }
}
