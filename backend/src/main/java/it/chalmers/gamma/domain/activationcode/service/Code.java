package it.chalmers.gamma.domain.activationcode.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Code implements Serializable {

    @JsonValue
    @Column(name = "code")
    @Pattern(regexp = "^([0-9]{8})$")
    private String value;

    protected Code() { }

    private Code(String value) {
        this.value = value;
    }

    public static Code generate() {
        return new Code(TokenUtils.generateToken(8, TokenUtils.CharacterTypes.NUMBERS));
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Code code = (Code) o;
        return Objects.equals(value, code.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Code: " + this.value;
    }
}

