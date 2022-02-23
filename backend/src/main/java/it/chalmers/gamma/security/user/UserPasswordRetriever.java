package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.lang.Nullable;

//TODO: Add ArchUnit that only UserConfig can use this.
public interface UserPasswordRetriever {
    @Nullable
    Password getPassword(UserId id);

    class UserNotFoundException extends RuntimeException { }

}
