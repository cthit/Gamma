package it.chalmers.gamma.domain;

import it.chalmers.gamma.internal.user.service.Password;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Min;

public class UnencryptedPassword {

    @Min(8)
    private String password;

    public static UnencryptedPassword valueOf(String value) {
        UnencryptedPassword unencryptedPassword = new UnencryptedPassword();
        unencryptedPassword.password = value;
        return unencryptedPassword;
    }

    public Password encrypt(PasswordEncoder passwordEncoder) {
        return new Password(passwordEncoder.encode(this.password));
    }

}
