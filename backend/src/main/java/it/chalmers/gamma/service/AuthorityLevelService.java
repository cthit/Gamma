package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.repository.AuthorityLevelRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AuthorityLevelService {
    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public AuthorityLevel addAuthorityLevel(String level) {
        AuthorityLevel authorityLevel = new AuthorityLevel();
        authorityLevel.setAuthorityLevel(level);
        this.authorityLevelRepository.save(authorityLevel);
        return authorityLevel;
    }

    public boolean authorityLevelExists(String authorityLevel) {
        return this.authorityLevelRepository.findByAuthorityLevel(authorityLevel.toUpperCase()) != null;
    }

    public boolean authorityLevelExists(UUID id) {
        return this.authorityLevelRepository.existsById(id);
    }

    public AuthorityLevel getAuthorityLevel(UUID authorityLevel) {
        return this.authorityLevelRepository.findById(authorityLevel).orElse(null);
    }

    public AuthorityLevel getAuthorityLevel(String authorityLevel) {
        return this.authorityLevelRepository.findByAuthorityLevel(authorityLevel);
    }

    public List<AuthorityLevel> getAllAuthorityLevels() {
        return this.authorityLevelRepository.findAll();
    }

    public void removeAuthorityLevel(UUID id) {
        this.authorityLevelRepository.deleteById(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorityLevelService that = (AuthorityLevelService) o;
        return this.authorityLevelRepository.equals(that.authorityLevelRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.authorityLevelRepository);
    }

    @Override
    public String toString() {
        return "AuthorityLevelService{"
            + "authorityLevelRepository=" + this.authorityLevelRepository
            + '}';
    }
}
