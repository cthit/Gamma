package it.chalmers.gamma.domain.group;

public record EmailPrefix(String value) {

    public EmailPrefix {
        if (value != null && !value.matches("^$|^(?:\\w+|\\w+\\.\\w+)+$")) {
            throw new IllegalArgumentException("Email prefix most be letters of a - z, and each word must be seperated by a dot");
        }
    }

    public static EmailPrefix empty() {
        return new EmailPrefix("");
    }
}
