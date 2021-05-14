package it.chalmers.gamma.internal.supergrouptype.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.util.Locale;

@Embeddable
public class SuperGroupTypeName extends Id<String> implements DTO {

    @Column(name = "name")
    @JsonValue
    @Pattern(regexp = "^([a-z]{30})$")
    private String value;

    protected SuperGroupTypeName() { }

    private SuperGroupTypeName(String value) {
        this.value = value.toLowerCase(Locale.ROOT);
    }

    public static SuperGroupTypeName valueOf(String name) {
        return new SuperGroupTypeName(name);
    }

    @Override
    protected String get() {
        return this.value;
    }
}
