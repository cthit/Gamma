package it.chalmers.gamma.domain.common;

public record TextValue(String value) {

    public TextValue {
        if (value == null) {
            throw new NullPointerException("Text value cannot be null");
        } else if (value.length() > 2048) {
            throw new IllegalArgumentException("Text value max length is 2048");
        }
    }

    public static TextValue empty() {
        return new TextValue("");
    }

    public static TextValue valueOf(String value) {
        return new TextValue(value);
    }

}
