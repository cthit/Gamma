package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EmailPrefix implements Serializable {

    /**
     * Must match the pattern of words without spaces with dots between
     * Such as ordf.prit, ita.styrit or kassor
     * Or it must be empty.
     */
    @JsonValue
    @Column(name = "email_prefix")
    private String value;

    protected EmailPrefix() { }

    private EmailPrefix(String value) {
        if (value != null && !value.matches("^$|^(?:\\w+|\\w+\\.\\w+)+$")) {
            throw new IllegalArgumentException("Email prefix most be letters of a - z, and each word must be seperated by a dot");
        }

        this.value = value;
    }

    public static EmailPrefix valueOf(String s) {
        return new EmailPrefix(s);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
