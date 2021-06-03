package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "^$|^(?:\\w+|\\w+\\.\\w+)+$")
    private String value;

    protected EmailPrefix() { }

    protected EmailPrefix(String value) {
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
