package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.Authority;
import it.chalmers.delta.db.entity.AuthorityLevel;
import it.chalmers.delta.db.entity.FKITSuperGroup;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.db.entity.Post;
import it.chalmers.delta.db.entity.pk.AuthorityPK;
import it.chalmers.delta.db.repository.AuthorityRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;

    public AuthorityService(AuthorityRepository authorityRepository,
                            FKITGroupToSuperGroupService fkitGroupToSuperGroupService) {
        this.authorityRepository = authorityRepository;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
    }

    public void setAuthorityLevel(FKITSuperGroup group, Post post, AuthorityLevel authorityLevel) {
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(
                group, post
        );
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
            List<Authority> authorities = this.fkitGroupToSuperGroupService
                    .getSuperGroups(membership.getId().getFKITGroup())
                    .stream().map(group -> this.getAuthorityLevel(group,
                            membership.getId().getPost())).collect(Collectors.toList());
            for (Authority authority : authorities) {
                if (authority != null) {
                    Calendar start = membership.getId().getFKITGroup().getBecomesActive();
                    Calendar end = membership.getId().getFKITGroup().getBecomesInactive();
                    Calendar now = Calendar.getInstance();
                    if (now.after(start) && now.before(end)) {
                        authorityLevels.add(authority.getAuthorityLevel());
                    }
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

    public boolean authorityExists(UUID id) {
        return this.authorityRepository.existsById(id);
    }
}
