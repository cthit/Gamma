package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SettingsId extends Id<Integer> {

    @JsonValue
    @Column(name = "id")
    private int value;

    protected SettingsId() { }

    @Override
    protected Integer get() {
        return this.value;
    }
}
