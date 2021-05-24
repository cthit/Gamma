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
    @Column(name = "key")
    private final String value;

    public ApiKeyToken() {
        value = TokenUtils.generateToken(50,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    public ApiKeyToken(String value) {
        this.value = value;
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
}
