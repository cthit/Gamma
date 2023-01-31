package it.chalmers.gamma.app.group.domain;

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
