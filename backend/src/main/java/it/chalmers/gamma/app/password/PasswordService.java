package it.chalmers.gamma.app.password;

import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Password encrypt(UnencryptedPassword unencryptedPassword) {
        return new Password(this.passwordEncoder.encode(unencryptedPassword.password()));
    }

    public boolean matches(UnencryptedPassword unencryptedPassword, Password password) {
        return this.passwordEncoder.matches(unencryptedPassword.password(), password.value());
    }
}
