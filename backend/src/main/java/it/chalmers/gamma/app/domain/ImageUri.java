package it.chalmers.gamma.app.domain;

public record ImageUri(String value) {

    public ImageUri {
        if (value == null) {
            throw new NullPointerException("Image Uri cannot be null");
        } else if (!(value.endsWith(".png")
                || value.endsWith(".jpg")
                || value.endsWith(".gif")
                || value.endsWith(".jpeg"))) {
            throw new IllegalArgumentException("Image uri must end with .png, .jpg, .jpeg or .gif");
        }
    }

}
