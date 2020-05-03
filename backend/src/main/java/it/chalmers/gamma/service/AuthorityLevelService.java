package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.repository.AuthorityLevelRepository;

import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.response.authority.AuthorityLevelDoesNotExistException;
import it.chalmers.gamma.util.UUIDUtil;
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
        if (this.authorityLevelRepository.existsByAuthorityLevel(authorityLevel)) {
            return true;
        }

        return UUIDUtil.validUUID(authorityLevel)
            && this.authorityLevelRepository.existsById(UUID.fromString(authorityLevel));
    }

    public boolean authorityLevelExists(UUID id) {
        return this.authorityLevelRepository.existsById(id);
    }

    public AuthorityLevelDTO getAuthorityLevelDTO(String authorityLevel) {
        if (UUIDUtil.validUUID(authorityLevel)) {
            return this.authorityLevelRepository.findById(UUID.fromString(authorityLevel))
                    .orElseThrow(AuthorityLevelDoesNotExistException::new).toDTO();
        }
        return this.authorityLevelRepository.findByAuthorityLevel(authorityLevel.toLowerCase())
                .orElseThrow(AuthorityLevelDoesNotExistException::new).toDTO();
    }


    public List<AuthorityLevelDTO> getAllAuthorityLevels() {
        return this.authorityLevelRepository.findAll().stream().map(AuthorityLevel::toDTO).collect(Collectors.toList());
    }

    public void removeAuthorityLevel(UUID id) {
        this.authorityLevelRepository.deleteById(id);
    }

    protected AuthorityLevel getAuthorityLevel(AuthorityLevelDTO authorityLevelDTO) {
        return this.authorityLevelRepository.findById(authorityLevelDTO.getId()).orElse(null);
    }
}
