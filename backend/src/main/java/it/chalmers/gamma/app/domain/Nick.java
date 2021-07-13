package it.chalmers.gamma.app.domain;

public record Nick(String value) {

    public Nick {
        if (value == null) {
            throw new NullPointerException("Nick cannot be null");
        } else if (value.length() < 1 || value.length() > 30) {
            throw new IllegalArgumentException("Nick length must be between 1 and 30");
        }
    }

}
