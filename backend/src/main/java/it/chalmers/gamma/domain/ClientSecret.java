package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ClientSecret {

    @Column(name = "client_secret")
    private final String value;

    public ClientSecret() {
        value = "{noop}" + TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    public ClientSecret(String value) {
        this.value = value;
    }

    public static ClientSecret valueOf(String value)  {
        return new ClientSecret(value);
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientSecret clientId = (ClientSecret) o;
        return Objects.equals(value, clientId.value);
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
