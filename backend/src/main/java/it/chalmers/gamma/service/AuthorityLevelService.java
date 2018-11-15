package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.repository.AuthorityLevelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorityLevelService {
    AuthorityLevelRepository authorityLevelRepository;
    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository){
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public void addAuthorityLevel(String level){
        AuthorityLevel authorityLevel = new AuthorityLevel();
        authorityLevel.setAuthorityLevel(level);
        authorityLevelRepository.save(authorityLevel);
    }

    public boolean authorityLevelExists(String authorityLevel){
        if(authorityLevelRepository.findByAuthorityLevel(authorityLevel.toUpperCase()) == null){
            return false;
        }
        return true;
    }

    public AuthorityLevel getAuthorityLevel(UUID authorityLevel){
        return authorityLevelRepository.findById(authorityLevel).orElse(null);
    }

    public List<AuthorityLevel> getAllAuthorityLevels(){
        return authorityLevelRepository.findAll();
    }

}
