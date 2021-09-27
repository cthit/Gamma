package it.chalmers.gamma.app.domain.user;

public record UnencryptedPassword(String password) {

    public UnencryptedPassword {
        if (password == null) {
            throw new NullPointerException("Password cannot be null");
        } else if (password.length() < 8) {
            throw new IllegalArgumentException("Password length must be atleast 8");
        }

    }
}
