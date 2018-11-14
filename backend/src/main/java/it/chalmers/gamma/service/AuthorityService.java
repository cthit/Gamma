package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import it.chalmers.gamma.db.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository){
        this.authorityRepository = authorityRepository;
    }

    public void setAuthorityLevel(FKITGroup group, Post post, AuthorityLevel authorityLevel){
        Authority authority = authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
        if(authority == null) {
            authority = new Authority();
            AuthorityPK pk = new AuthorityPK();
            pk.setFkitGroup(group);
            pk.setPost(post);
            authority.setId(pk);
        }
        System.out.println(authorityLevel);
        authority.setAuthorityLevel((AuthorityLevel) authorityLevel);
        System.out.println(authority);
        authorityRepository.save(authority);
    }

    public Authority getAuthorityLevel(FKITGroup group, Post post){
        return authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
    }

    public void removeAuthority(FKITGroup group, Post post){
        Authority authority = authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
        authorityRepository.delete(authority);
    }
    public List<AuthorityLevel> getAuthorities(List<Membership> memberships){
        List<AuthorityLevel> authorityLevels = new ArrayList<>();
        for(Membership membership : memberships){
            System.out.println(authorityRepository.findAll());
            Authority authority = getAuthorityLevel(membership.getId().getFKITGroup(), membership.getPost());
            if(authority != null) {
                authorityLevels.add(authority.getAuthorityLevel());
            }
        }
        return authorityLevels;
    }

}
