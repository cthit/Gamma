package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import it.chalmers.gamma.db.repository.AuthorityRepository;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;
    private final FKITSuperGroupService fkitSuperGroupService;
    private final PostService postService;
    private final AuthorityLevelService authorityLevelService;
    private final MembershipService membershipService;

    public AuthorityService(AuthorityRepository authorityRepository,
                            FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
                            FKITSuperGroupService fkitSuperGroupService,
                            PostService postService,
                            AuthorityLevelService authorityLevelService, MembershipService membershipService) {
        this.authorityRepository = authorityRepository;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
        this.membershipService = membershipService;
    }

    public void setAuthorityLevel(FKITSuperGroupDTO groupDTO, PostDTO postDTO, AuthorityLevelDTO authorityLevelDTO) {
        Post post = this.postService.getPost(postDTO);
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        AuthorityLevel authorityLevel = this.authorityLevelService.getAuthorityLevel(authorityLevelDTO);
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(
                group,
                post
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

    protected List<GrantedAuthority> getGrantedAuthorities(ITUserDTO details) {
        List<MembershipDTO> memberships = this.membershipService.getMembershipsByUser(details);
        //  for (MembershipDTO membership : memberships) {
        //      AuthorityLevel authorityLevel = this.authorityLevelService
        //              .getAuthorityLevel(this.authorityLevelService.getAuthorityLevelDTO(
        //                      membership.getFkitGroupDTO().getId().toString()));
        //      if (authorityLevel != null) {
        //          authorities.add(authorityLevel);
        //      }
        //  }
        return new ArrayList<>(this.getAuthorities(memberships));
    }


    // TODO Check for name?
    public boolean authorityExists(String id) {
        return this.authorityRepository.existsById(UUID.fromString(id));
    }

    public AuthorityDTO getAuthorityLevel(FKITSuperGroupDTO groupDTO, PostDTO postDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        Post post = this.postService.getPost(postDTO);
        return this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post).toDTO();
    }

    public void removeAuthority(FKITSuperGroupDTO groupDTO, PostDTO postDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        Post post = this.postService.getPost(postDTO);
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post);
        this.authorityRepository.delete(authority);
    }

    @Transactional
    public void removeAuthority(UUID id) {
        this.authorityRepository.deleteByInternalId(id);
    }

    public List<AuthorityLevelDTO> getAuthorities(List<MembershipDTO> memberships) {
        List<AuthorityLevelDTO> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            List<AuthorityDTO> authorities = this.fkitGroupToSuperGroupService
                    .getSuperGroups(membership.getFkitGroupDTO())
                    .stream().map(group -> this.getAuthorityLevel(group,
                            membership.getPost())).collect(Collectors.toList());
            for (AuthorityDTO authority : authorities) {
                if (authority != null) {
                    Calendar start = membership.getFkitGroupDTO().getBecomesActive();
                    Calendar end = membership.getFkitGroupDTO().getBecomesInactive();
                    Calendar now = Calendar.getInstance();
                    if (now.after(start) && now.before(end)) {
                        authorityLevels.add(authority.getAuthorityLevelDTO());
                    }
                }
            }
        }
        return authorityLevels;
    }

    public List<AuthorityDTO> getAllAuthorities() {
        return this.authorityRepository.findAll().stream().map(Authority::toDTO).collect(Collectors.toList());
    }

    public List<AuthorityDTO> getAllAuthoritiesWithAuthorityLevel(AuthorityLevelDTO authorityLevelDTO) {
        AuthorityLevel authorityLevel = this.authorityLevelService.getAuthorityLevel(authorityLevelDTO);
        return this.authorityRepository.findAllByAuthorityLevel(authorityLevel)
                .stream().map(Authority::toDTO).collect(Collectors.toList());
    }

    public AuthorityDTO getAuthority(UUID id) {
        return this.authorityRepository.findByInternalId(id).toDTO();
    }


}
