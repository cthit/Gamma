package it.chalmers.gamma.authoritylevel.service;

import it.chalmers.gamma.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevel;
import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelNotFoundException;
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
        return this.authorityLevelRepository.existsByAuthorityLevel(authorityLevel.value);
    }

    public boolean authorityLevelExists(UUID authorityLevelId) {
        return this.authorityLevelRepository.existsById(authorityLevelId);
    }

    public AuthorityLevelDTO getAuthorityLevel(UUID id) throws AuthorityLevelNotFoundException {
        return toDTO(getAuthorityLevelEntity(id));
    }

    protected AuthorityLevel getAuthorityLevelEntity(UUID id) throws AuthorityLevelNotFoundException {
        return this.authorityLevelRepository.findById(id).orElseThrow(AuthorityLevelNotFoundException::new);
    }

    protected AuthorityLevel getAuthorityLevelEntity(AuthorityLevelDTO authorityLevel) throws AuthorityLevelNotFoundException {
        return getAuthorityLevelEntity(authorityLevel.getId());
    }

    public List<AuthorityLevelDTO> getAllAuthorityLevels() {
        return this.authorityLevelRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    protected AuthorityLevelDTO toDTO(AuthorityLevel authorityLevel) {
        return new AuthorityLevelDTO(
                authorityLevel.getId(),
                authorityLevel.getAuthority()
        );
    }
}
