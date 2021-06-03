package it.chalmers.gamma.internal.authority.level.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityLevelService {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public void create(AuthorityLevelName levelName) throws AuthorityLevelAlreadyExistsException {
        try {
            this.authorityLevelRepository.save(new AuthorityLevelEntity(levelName));
        } catch(IllegalArgumentException e) {
            throw new AuthorityLevelAlreadyExistsException();
        }
    }

    public void delete(AuthorityLevelName name) throws AuthorityLevelNotFoundException {
        this.authorityLevelRepository.deleteById(name.value);
    }

    public List<AuthorityLevelName> getAll() {
        return this.authorityLevelRepository.findAll().stream().map(AuthorityLevelEntity::get).collect(Collectors.toList());
    }

    public static class AuthorityLevelAlreadyExistsException extends Exception { }
    public static class AuthorityLevelNotFoundException extends Exception { }

}
