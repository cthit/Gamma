package it.chalmers.gamma.app.image.domain;

public interface ImageService {

    ImageUri saveImage(Image image) throws ImageCouldNotBeSavedException;

    void removeImage(ImageUri imageUri) throws ImageCouldNotBeRemovedException;

    ImageDetails getImage(ImageUri imageUri);

    record ImageDetails(byte[] data, String type) {
    }

    class ImageCouldNotBeRemovedException extends Exception {
    }

    class ImageCouldNotBeSavedException extends Exception {
        public ImageCouldNotBeSavedException(String message) {
            super(message);
        }

        public ImageCouldNotBeSavedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
