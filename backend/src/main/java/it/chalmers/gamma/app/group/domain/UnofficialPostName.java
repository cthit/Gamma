package it.chalmers.gamma.app.group.domain;

import java.util.Objects;

public record UnofficialPostName(String value) {

    public UnofficialPostName {
        if ("".equals(value)) {
            value = null;
        }
    }

    public static UnofficialPostName none() {
        return new UnofficialPostName(null);
    }

}
