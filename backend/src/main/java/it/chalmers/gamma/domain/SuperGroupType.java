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

    @Column(name = "super_group_type_name")
    @JsonValue
    private String value;

    protected SuperGroupType() { }

    private SuperGroupType(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Super group type cannot be null");
        } else if (!value.matches("^([a-z]{5,30})$")) {
            throw new IllegalArgumentException("Super group type must be made using letters with length between 5 - 30");
        }

        this.value = value;
    }

    public static SuperGroupType valueOf(String name) {
        return new SuperGroupType(name);
    }

    @Override
    protected String get() {
        return this.value;
    }
}
