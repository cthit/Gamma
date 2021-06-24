package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ImageUri implements Serializable {

    @JsonValue
    @Column
    private String value;

    protected ImageUri() {

    }

    private ImageUri(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Image Uri cannot be null");
        } else if (!(value.endsWith(".png")
                || value.endsWith(".jpg")
                || value.endsWith(".gif")
                || value.endsWith(".jpeg"))) {
            throw new IllegalArgumentException("Image uri must end with .png, .jpg, .jpeg or .gif");
        }

        this.value = value;
    }

    public static ImageUri valueOf(String value) {
        return new ImageUri(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageUri lastName = (ImageUri) o;
        return Objects.equals(value, lastName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
