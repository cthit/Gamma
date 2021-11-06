package it.chalmers.gamma.app.usecase;

import it.chalmers.gamma.app.authentication.ApiAuthenticated;
import it.chalmers.gamma.app.authentication.Unauthenticated;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserAuthority;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.app.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accessGuard")
public class AccessGuardUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessGuardUseCase.class);
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

    public class AccessCheckerEvaluation extends AccessChecker {

        private boolean result;

        private AccessCheckerEvaluation(boolean result) {
            this.result = result;
        }
    }

    public class AccessCheckerEvaluationOr extends AccessCheckerEvaluation {

        private AccessCheckerEvaluationOr(boolean result) {
            super(result);
        }

    }

    public class AccessCheckerEvaluationAnd extends AccessCheckerEvaluation {
        private AccessCheckerEvaluationAnd(boolean result) {
            super(result);
        }
    }

    public class AccessChecker {

        private boolean success = false;
        private AccessCheckerLink link;

        public AccessCheckerLink isAdmin() {
            if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated userAuthenticated) {
                User user = userAuthenticated.get();
                List<AuthorityLevelName> authorities = authorityLevelRepository.getByUser(user.id())
                        .stream()
                        .map(UserAuthority::authorityLevelName)
                        .toList();
                if (authorities.contains(adminAuthority)) {
                    success = true;
                }
            }

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink passwordCheck(String password) {
            if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated userAuthenticated) {
                User user = userAuthenticated.get();
                if (passwordService.matches(new UnencryptedPassword(password), user.password())) {
                    success = true;
                }
            }

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink isApi(ApiKeyType apiKeyType) {
            if (authenticatedService instanceof ApiAuthenticated apiAuthenticated) {
                success = apiAuthenticated.get().keyType() == apiKeyType;
            }

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink signedInUserMemberOfGroup(Group group) {
            if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated internalUserAuthenticated) {
                User user = internalUserAuthenticated.get();
                success = group.groupMembers().stream().anyMatch(groupMember -> groupMember.user().equals(user));
            }

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink isSignedIn() {
            success = authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated;

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink isUser(User user) {
            if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated internalUserAuthenticated) {
                success = internalUserAuthenticated.get().equals(user);
            }

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink notSignedIn() {
            success = authenticatedService.getAuthenticated() instanceof Unauthenticated;

            return new AccessCheckerLink(this);
        }

        public AccessCheckerLink isClientApi() {
            if (authenticatedService.getAuthenticated() instanceof ApiAuthenticated apiAuthenticated) {
                success = apiAuthenticated.get().keyType() == ApiKeyType.CLIENT;
            }

            return new AccessCheckerLink(this);
        }

    }

    public class AccessCheckerLink {

        private final AccessChecker previous;
        private LinkType linkType;

        private enum LinkType {
            AND, OR
        }

        private AccessCheckerLink(AccessChecker previous) {
            this.previous = previous;
        }

        public AccessChecker or() {
            this.linkType = LinkType.OR;

            AccessChecker accessChecker = new AccessChecker();
            accessChecker.link = this;
            return accessChecker;
        }

        public AccessChecker and() {
            this.linkType = LinkType.AND;

            AccessChecker accessChecker = new AccessChecker();
            accessChecker.link = this;
            return accessChecker;
        }

        public void ifNotThrow() {
            boolean success = false;
            AccessChecker second = this.previous;
            AccessChecker first = null;
            if (second.link != null) {
                if (second.link.previous.link != null) {
                    throw new IllegalStateException("AccessChecker cannot have more than one .or() or .and()");
                }

                first = second.link.previous;
                if (second.link.linkType == LinkType.AND) {
                    success = first.success && second.success;
                } else if (second.link.linkType == LinkType.OR) {
                    success = first.success || second.success;
                }
            } else {
                success = second.success;
            }

            if (!success) {
                LOGGER.error("AccessDenied, first layer broke");
                throw new AccessDeniedException();
            }
        }

    }

    public AccessChecker require() {
        return new AccessChecker();
    }

    public static class AccessDeniedException extends RuntimeException { }

}

