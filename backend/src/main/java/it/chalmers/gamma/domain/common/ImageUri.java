package it.chalmers.gamma.domain.common;

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

    //TODO: These defaults shouldn't be here
    public static ImageUri defaultGroupBanner() {
        return new ImageUri("default_group_banner.jpg");
    }

    public static ImageUri defaultGroupAvatar() {
        return new ImageUri("default_group_avatar.jpg");
    }

    public static ImageUri defaultUserAvatar() {
        return new ImageUri("default_user_avatar.jpg");
    }

}
