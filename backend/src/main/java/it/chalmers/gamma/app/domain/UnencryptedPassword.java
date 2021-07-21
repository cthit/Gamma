package it.chalmers.gamma.app.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

//TODO: whats the purpose?
public class UnencryptedPassword {

    private final String password;

    private UnencryptedPassword(String value) {
        if (value == null) {
            throw new NullPointerException("Password cannot be null");
        } else if (value.length() < 8) {
            throw new IllegalArgumentException("Password length must be atleast 8");
        }

        this.password = value;
    }

    public static UnencryptedPassword valueOf(String value) {
        return new UnencryptedPassword(value);
    }

    public Password encrypt(PasswordEncoder passwordEncoder) {
        return new Password(passwordEncoder.encode(this.password));
    }

}
