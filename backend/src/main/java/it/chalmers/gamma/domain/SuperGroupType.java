package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.util.Locale;

@Embeddable
public class SuperGroupType extends Id<String> implements DTO {

    @Column(name = "name")
    @JsonValue
    @Pattern(regexp = "^([a-z]{30})$")
    private String value;

    protected SuperGroupType() { }

    private SuperGroupType(String value) {
        this.value = value.toLowerCase(Locale.ROOT);
    }

    public static SuperGroupType valueOf(String name) {
        return new SuperGroupType(name);
    }

    @Override
    protected String get() {
        return this.value;
    }
}
