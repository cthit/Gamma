package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.repository.AuthorityLevelRepository;

import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AuthorityLevelService {
    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public AuthorityLevelDTO addAuthorityLevel(String level) {
        AuthorityLevel authorityLevel = new AuthorityLevel();
        authorityLevel.setAuthorityLevel(level);
        this.authorityLevelRepository.save(authorityLevel);
        return authorityLevel.toDTO();
    }

    public boolean authorityLevelExists(String authorityLevel) {
        return this.authorityLevelRepository.findByAuthorityLevel(authorityLevel.toUpperCase()) != null;
    }

    public boolean authorityLevelExists(UUID id) {
        return this.authorityLevelRepository.existsById(id);
    }

    public AuthorityLevelDTO getAuthorityLevel(UUID authorityLevel) {
        return this.authorityLevelRepository.findById(authorityLevel).map(AuthorityLevel::toDTO).orElse(null);
    }

    public AuthorityLevelDTO getAuthorityLevel(String authorityLevel) {
        return this.authorityLevelRepository.findByAuthorityLevel(authorityLevel).toDTO();
    }

    public List<AuthorityLevelDTO> getAllAuthorityLevels() {
        return this.authorityLevelRepository.findAll().stream().map(AuthorityLevel::toDTO).collect(Collectors.toList());
    }

    public void removeAuthorityLevel(UUID id) {
        this.authorityLevelRepository.deleteById(id);
    }

}
