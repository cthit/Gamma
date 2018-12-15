package it.chalmers.gamma.service;

;
import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import it.chalmers.gamma.db.repository.AuthorityRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public void setAuthorityLevel(FKITSuperGroup group, Post post, AuthorityLevel authorityLevel) {
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post);
        if (authority == null) {
            authority = new Authority();
            AuthorityPK pk = new AuthorityPK();
            pk.setFkitGroup(group);
            pk.setPost(post);
            authority.setId(pk);
        }
        authority.setAuthorityLevel(authorityLevel);
        this.authorityRepository.save(authority);
    }

    public Authority getAuthorityLevel(FKITSuperGroup group, Post post) {
        return this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post);
    }

    public void removeAuthority(FKITSuperGroup group, Post post) {
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post);
        this.authorityRepository.delete(authority);
    }

    @Transactional
    public void removeAuthority(UUID id) {
        this.authorityRepository.deleteByInternalId(id);
    }

    public List<AuthorityLevel> getAuthorities(List<Membership> memberships) {
        List<AuthorityLevel> authorityLevels = new ArrayList<>();
        for (Membership membership : memberships) {
            Authority authority = getAuthorityLevel(
                    membership.getId().getFKITGroup().getSuperGroup(), membership.getPost());
            if (authority != null) {
                Calendar start = membership.getId().getFKITGroup().getBecomesActive();
                Calendar end = membership.getId().getFKITGroup().getBecomesInactive();
                Calendar now = Calendar.getInstance();
                if (now.after(start) && now.before(end)) {
                    authorityLevels.add(authority.getAuthorityLevel());
                }
            }
        }
        return authorityLevels;
    }

    public List<Authority> getAllAuthorities() {
        return this.authorityRepository.findAll();
    }

    public List<Authority> getAllAuthoritiesWithAuthorityLevel(AuthorityLevel authorityLevel) {
        return this.authorityRepository.findAllByAuthorityLevel(authorityLevel);
    }

    public Authority getAuthority(UUID id) {
        return this.authorityRepository.findByInternalId(id);
    }

}
