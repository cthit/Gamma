package it.chalmers.gamma.app.domain;

public record EmailPrefix(String value) {

    public EmailPrefix {
        if (value != null && !value.matches("^$|^(?:\\w+|\\w+\\.\\w+)+$")) {
            throw new IllegalArgumentException("Email prefix most be letters of a - z, and each word must be seperated by a dot");
        }
    }

}
