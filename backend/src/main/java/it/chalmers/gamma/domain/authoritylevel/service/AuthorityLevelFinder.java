package it.chalmers.gamma.domain.authoritylevel.service;

import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.data.db.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.data.db.AuthorityLevelRepository;
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
        return this.authorityLevelRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    protected AuthorityLevelName toDTO(AuthorityLevel authorityLevel) {
        return new AuthorityLevelName(authorityLevel.getAuthorityLevel());
    }
}
