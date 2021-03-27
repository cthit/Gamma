package it.chalmers.gamma.util.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Cid implements Serializable, DTO, Id {

    @JsonValue
    @Column(name = "cid")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cid cid = (Cid) o;
        return Objects.equals(value, cid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Cid: " + value;
    }
}
