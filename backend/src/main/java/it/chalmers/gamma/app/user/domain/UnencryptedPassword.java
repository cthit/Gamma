package it.chalmers.gamma.app.user.domain;

public record UnencryptedPassword(String value) {

    public UnencryptedPassword {
        if (value == null) {
            throw new NullPointerException("Password cannot be null");
        } else if (value.length() < 8) {
            throw new IllegalArgumentException("Password length must be atleast 8");
        }

    }
}
