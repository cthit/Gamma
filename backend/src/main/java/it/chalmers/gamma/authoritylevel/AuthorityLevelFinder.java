package it.chalmers.gamma.authoritylevel;

import it.chalmers.gamma.authoritylevel.response.AuthorityLevelDoesNotExistException;
import it.chalmers.gamma.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuthorityLevelFinder {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelFinder(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public Optional<AuthorityLevelDTO> getAuthorityLevel(UUID id) {
        Optional<AuthorityLevel> authorityLevelEntity = getAuthorityLevelEntity(id);
        Optional<AuthorityLevelDTO> authorityLevel = Optional.empty();

        if(authorityLevelEntity.isPresent()) {
            authorityLevel = authorityLevelEntity.map(this::toDTO);
        }

        return authorityLevel;
    }

    protected Optional<AuthorityLevel> getAuthorityLevelEntity(UUID id) {
        return this.authorityLevelRepository.findById(id);
    }

    protected Optional<AuthorityLevel> getAuthorityLevelEntity(AuthorityLevelDTO authorityLevel) {
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
