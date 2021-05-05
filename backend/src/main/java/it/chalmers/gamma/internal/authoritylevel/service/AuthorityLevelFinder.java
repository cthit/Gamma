package it.chalmers.gamma.internal.authoritylevel.service;

import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorityLevelFinder implements GetAllEntities<AuthorityLevelName> {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelFinder(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public boolean authorityLevelExists(AuthorityLevelName authorityLevel) {
        return this.authorityLevelRepository.existsById(authorityLevel.value);
    }

    public List<AuthorityLevelName> getAll() {
        return this.authorityLevelRepository.findAll().stream().map(AuthorityLevel::toDTO).collect(Collectors.toList());
    }

}
