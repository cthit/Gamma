package it.chalmers.gamma.adapter.secondary.password;

import it.chalmers.gamma.app.service.PasswordService;
import it.chalmers.gamma.app.domain.user.Password;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceAdapter implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Password encrypt(UnencryptedPassword unencryptedPassword) {
        return new Password(this.passwordEncoder.encode(unencryptedPassword.password()));
    }

    @Override
    public boolean matches(UnencryptedPassword unencryptedPassword, Password password) {
        return this.passwordEncoder.matches(unencryptedPassword.password(), password.value());
    }
}
