package it.chalmers.gamma.domain.group;

import com.fasterxml.jackson.annotation.JsonCreator;

public record UnofficialPostName(String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public UnofficialPostName {

    }

}
