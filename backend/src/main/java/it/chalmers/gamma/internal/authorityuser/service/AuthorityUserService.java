package it.chalmers.gamma.internal.authorityuser.service;

import it.chalmers.gamma.domain.AuthorityUser;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.internal.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityUserService {

    private final AuthorityUserRepository repository;
    private final UserService userService;

    public AuthorityUserService(AuthorityUserRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public void create(AuthorityUserShallowDTO authority) throws AuthorityUserNotFoundException {
        try {
            this.repository.save(
                    new AuthorityUserEntity(authority)
            );
        } catch (IllegalArgumentException e) {
            throw new AuthorityUserNotFoundException();
        }
    }

    public void delete(AuthorityUserPK id) throws AuthorityUserNotFoundException {
        try {
            this.repository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new AuthorityUserNotFoundException();
        }
    }

    public List<AuthorityUser> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(AuthorityUserEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<AuthorityUser> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return this.repository.findAuthorityUserEntitiesById_AuthorityLevelName(authorityLevelName)
                .stream()
                .map(AuthorityUserEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private AuthorityUser fromShallow(AuthorityUserShallowDTO authorityUserShallowDTO) {
        try {
            return new AuthorityUser(
                    new UserRestricted(this.userService.get(authorityUserShallowDTO.userId())),
                    authorityUserShallowDTO.authorityLevelName()
            );
        } catch (UserService.UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsBy(AuthorityLevelName name) {
        return this.repository.existsById_AuthorityLevelName(name);
    }

    public List<AuthorityLevelName> getByUser(UserId userId) {
        return this.repository.findAuthorityUserEntitiesById_UserId(userId)
                .stream()
                .map(AuthorityUserEntity::toDTO)
                .map(AuthorityUserShallowDTO::authorityLevelName)
                .collect(Collectors.toList());
    }

    public static class AuthorityUserNotFoundException extends Exception { }

}
