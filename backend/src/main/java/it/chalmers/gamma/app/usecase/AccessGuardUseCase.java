package it.chalmers.gamma.app.usecase;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserAuthority;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.app.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.service.PasswordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accessGuard")
public class AccessGuardUseCase {

    private static final AuthorityLevelName adminAuthority = new AuthorityLevelName("admin");

    private final AuthenticatedService authenticatedService;
    private final PasswordService passwordService;
    private final AuthorityLevelRepository authorityLevelRepository;

    public AccessGuardUseCase(AuthenticatedService authenticatedService,
                              PasswordService passwordService,
                              AuthorityLevelRepository authorityLevelRepository) {
        this.authenticatedService = authenticatedService;
        this.passwordService = passwordService;
        this.authorityLevelRepository = authorityLevelRepository;
    }

    /**
     * Must have the authority level admin
     */
    public void requireIsAdmin() {
        if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated userAuthenticated) {
            User user = userAuthenticated.get();
            List<AuthorityLevelName> authorities = this.authorityLevelRepository.getByUser(user.id())
                    .stream()
                    .map(UserAuthority::authorityLevelName)
                    .toList();
            if (authorities.contains(adminAuthority)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

    public void requireIsAdminWithPassword(String password) throws AccessDeniedException {
        requireIsAdmin();
        if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated userAuthenticated) {
            User user = userAuthenticated.get();
            if (passwordService.matches(new UnencryptedPassword(password), user.password())) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

    public void requireIsAdminOrApi() throws AccessDeniedException {

    }

    public void requireUserIsPartOfGroup(Group group) throws AccessDeniedException {

    }

    public void requireSignedIn() throws AccessDeniedException {

    }

    public void requireIsUser(User user) throws AccessDeniedException {

    }

    public void requireNotSignedIn() throws AccessDeniedException {

    }

    public void requireIsClientApi() throws AccessDeniedException {

    }

    public static class AccessDeniedException extends RuntimeException { }

}

