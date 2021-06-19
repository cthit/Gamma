package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ApiKeyToken implements Serializable {

    @JsonValue
    @Column(name = "token")
    private final String value;

    protected ApiKeyToken() {
        value = TokenUtils.generateToken(
                150,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    private ApiKeyToken(String value) {
        this.value = value;
    }

    public static ApiKeyToken generate() {
        return new ApiKeyToken();
    }

    public static ApiKeyToken valueOf(String value)  {
        return new ApiKeyToken(value);
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKeyToken apiKeyToken = (ApiKeyToken) o;
        return Objects.equals(value, apiKeyToken.value);
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
