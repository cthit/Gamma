package it.chalmers.gamma.app.port.service;

import it.chalmers.gamma.app.domain.user.Password;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;

public interface PasswordService {
    Password encrypt(UnencryptedPassword unencryptedPassword);
    boolean matches(UnencryptedPassword unencryptedPassword, Password password);
}
