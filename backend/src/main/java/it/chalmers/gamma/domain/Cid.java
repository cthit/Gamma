package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class Cid extends Id<String> implements DTO {

    @JsonValue
    @Column(name = "cid")
    @Size(min = 4, max = 12, message = "CIDS_MUST_BE_BETWEEN_4_AND_12_CHARACTERS")
    private String value;

    protected Cid() {}

    public Cid(String value) {
        this.value = value.toLowerCase();
    }

    public static Cid valueOf(String cid) {
        return new Cid(cid);
    }

    public String get(){
        return this.value;
    }

}
