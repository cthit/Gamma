package it.chalmers.gamma.app.domain.user;

public record Password(String value) {

    public Password {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override

    public String toString() {
        return "<password redacted>";
    }
}

