package it.chalmers.gamma.domain.authoritylevel.service;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.data.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuthorityLevelFinder {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelFinder(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public boolean authorityLevelExists(AuthorityLevelName authorityLevel) {
        return this.authorityLevelRepository.existsById(authorityLevel.value);
    }

    public List<AuthorityLevelName> getAuthorityLevels() {
        return this.authorityLevelRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    protected AuthorityLevelName toDTO(AuthorityLevel authorityLevel) {
        return new AuthorityLevelName(authorityLevel.getAuthorityLevel());
    }
}
