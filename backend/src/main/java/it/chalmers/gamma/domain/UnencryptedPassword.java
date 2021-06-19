package it.chalmers.gamma.domain;

import it.chalmers.gamma.internal.user.service.Password;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Min;

public class UnencryptedPassword {

    private String password;

    protected UnencryptedPassword() {

    }

    private UnencryptedPassword(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Password cannot be null");
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
