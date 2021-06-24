package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AcceptanceYear implements Serializable {

    @JsonValue
    @Column(name = "acceptance_year")
    private int value;

    protected AcceptanceYear() { }

    protected AcceptanceYear(int value) {
        if (value < 2001) {
            throw new IllegalArgumentException("Acceptanc year cannot be less than 2001");
        }

        this.value = value;
    }

    public static AcceptanceYear valueOf(int value) {
        return new AcceptanceYear(value);
    }

}
