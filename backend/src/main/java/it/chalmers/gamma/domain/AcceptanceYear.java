package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class AcceptanceYear implements Serializable {

    @JsonValue
    @Column(name = "acceptance_year")
    @Size(min = 2001)
    private int value;

    //TODO: add better validation so that you cannot set your acceptanceYear in the future.
    public static AcceptanceYear valueOf(int value) {
        AcceptanceYear acceptanceYear = new AcceptanceYear();
        acceptanceYear.value = value;
        return acceptanceYear;
    }

}
