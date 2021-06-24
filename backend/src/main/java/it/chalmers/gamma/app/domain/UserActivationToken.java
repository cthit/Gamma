package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserActivationToken implements Serializable {

    @JsonValue
    @Column(name = "token")
    private String value;

    protected UserActivationToken() {
        this.value = TokenUtils.generateToken(8, TokenUtils.CharacterTypes.NUMBERS);
    }

    public static UserActivationToken generate() {
        return new UserActivationToken();
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
        UserActivationToken token = (UserActivationToken) o;
        return Objects.equals(value, token.value);
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

