package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.AuthoritySuperGroup;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthoritySuperGroupService {

    private final AuthoritySuperGroupRepository repository;
    private final SuperGroupService superGroupService;

    public AuthoritySuperGroupService(AuthoritySuperGroupRepository repository,
                                      SuperGroupService superGroupService) {
        this.repository = repository;
        this.superGroupService = superGroupService;
    }

    public void create(AuthoritySuperGroupShallowDTO authority) throws AuthoritySuperGroupAlreadyExistsException {
        try {
            this.repository.save(
                    new AuthoritySuperGroupEntity(authority)
            );
        } catch (IllegalArgumentException e) {
            throw new AuthoritySuperGroupAlreadyExistsException();
        }
    }

    public void delete(AuthoritySuperGroupPK id) throws AuthoritySuperGroupNotFoundException {
        try {
            this.repository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new AuthoritySuperGroupNotFoundException();
        }
    }

    public boolean existsBy(AuthorityLevelName name) {
        return this.repository.existsById_AuthorityLevelName(name);
    }

    public List<AuthoritySuperGroup> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(AuthoritySuperGroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<AuthoritySuperGroup> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return this.repository.findAuthoritySuperGroupEntitiesById_AuthorityLevelName(authorityLevelName)
                .stream()
                .map(AuthoritySuperGroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private AuthoritySuperGroup fromShallow(AuthoritySuperGroupShallowDTO authoritySuperGroup) {
        try {
            return new AuthoritySuperGroup(
                    this.superGroupService.get(authoritySuperGroup.superGroupId()),
                    authoritySuperGroup.authorityLevelName()
            );
        } catch (SuperGroupService.SuperGroupNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class AuthoritySuperGroupNotFoundException extends Exception { }
    public static class AuthoritySuperGroupAlreadyExistsException extends Exception { }

}
