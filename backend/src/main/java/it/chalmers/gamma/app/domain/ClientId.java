package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.adapter.secondary.jpa.util.Id;
import it.chalmers.gamma.util.TokenUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ClientId extends Id<String> implements Serializable {

    @JsonValue
    @Column(name = "client_id")
    private final String value;

    protected ClientId() {
        value = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
    }

    private ClientId(String value) {
        this.value = value;
    }

    public static ClientId generate() {
        return new ClientId();
    }

    public static ClientId valueOf(String value)  {
        return new ClientId(value);
    }

    public String get() {
        return this.value;
    }

}
