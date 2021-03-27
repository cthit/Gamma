package it.chalmers.gamma.domain.client.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClientId implements Serializable, Id {

    @JsonValue
    @Column(name = "client_id")
    private final String value;

    public ClientId() {
        value = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    public ClientId(String value) {
        this.value = value;
    }

    public static ClientId valueOf(String value)  {
        return new ClientId(value);
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientId clientId = (ClientId) o;
        return Objects.equals(value, clientId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
