package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.entity.DTO;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Locale;

@Embeddable
public class Cid extends Id<String> implements DTO {

    @JsonValue
    @Column(name = "cid")
    private String value;

    protected Cid() {}

    protected Cid(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Cid cannot be null");
        }

        value = value.toLowerCase(Locale.ROOT);

        if (!value.matches("^([a-z]{4,12})$")) {
            throw new IllegalArgumentException("Input: " + value + "; Cid length must be between 4 and 12, and only have letters between a - z");
        }

        this.value = value;
    }

    public static Cid valueOf(String cid) {
        return new Cid(cid);
    }

    public String get(){
        return this.value;
    }
}
