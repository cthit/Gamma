package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PasswordResetToken implements Serializable {

    @JsonValue
    @Column(name = "token")
    private final String value;

    protected PasswordResetToken() {
        value = TokenUtils.generateToken(
                75,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    private PasswordResetToken(String value) {
        this.value = value;
    }

    public static PasswordResetToken generate() {
        return new PasswordResetToken();
    }

    public static PasswordResetToken valueOf(String value)  {
        return new PasswordResetToken(value);
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetToken passwordResetToken = (PasswordResetToken) o;
        return Objects.equals(value, passwordResetToken.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
