package it.chalmers.gamma.authoritylevel;

import it.chalmers.gamma.authoritylevel.response.AuthorityLevelDoesNotExistException;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;
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


    @Transactional
    public void removeAuthorityLevel(UUID id) {
        this.authorityLevelRepository.deleteById(id);
    }

}
