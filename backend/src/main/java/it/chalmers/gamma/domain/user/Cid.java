package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.app.user.UserSignInIdentifier;

import java.io.Serializable;
import java.util.Locale;

public record Cid(String value) implements Id<String>, UserSignInIdentifier, Serializable {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Cid {
        if (value == null) {
            throw new NullPointerException("Cid cannot be null");
        }

        value = value.toLowerCase(Locale.ROOT);

        if (!value.matches("^([a-z]{4,12})$")) {
            throw new IllegalArgumentException("Input: " + value + "; Cid length must be between 4 and 12, and only have letters between a - z");
        }

    }

    public static Cid valueOf(String cid) {
        return new Cid(cid);
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
