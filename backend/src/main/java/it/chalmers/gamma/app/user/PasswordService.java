package it.chalmers.gamma.app.user;

import it.chalmers.gamma.domain.user.Password;
import it.chalmers.gamma.domain.user.UnencryptedPassword;

public interface PasswordService {
    Password encrypt(UnencryptedPassword unencryptedPassword);
    boolean matches(UnencryptedPassword unencryptedPassword, Password password);
}
