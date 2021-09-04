package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.repository.AuthorityRepository;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.response.authority.AuthorityDoesNotExistResponse;
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
    private final FKITSuperGroupService fkitSuperGroupService;
    private final PostService postService;
    private final AuthorityLevelService authorityLevelService;
    private final MembershipService membershipService;

    public AuthorityService(AuthorityRepository authorityRepository,
                            FKITSuperGroupService fkitSuperGroupService,
                            PostService postService,
                            AuthorityLevelService authorityLevelService, MembershipService membershipService) {
        this.authorityRepository = authorityRepository;
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
        this.membershipService = membershipService;
    }

    public AuthorityDTO createAuthority(
            FKITSuperGroupDTO groupDTO,
            PostDTO postDTO,
            AuthorityLevelDTO authorityLevelDTO) {

        Post post = this.postService.getPost(postDTO);
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        AuthorityLevel authorityLevel = this.authorityLevelService.getAuthorityLevel(authorityLevelDTO);
        Authority authority = this.authorityRepository.findByFkitSuperGroupAndPostAndAuthorityLevel(
            group,
            post,
            authorityLevel
        ).orElseGet(() -> null);
        if(authority != null) {
            return authority.toDTO();
        }
        authority = new Authority();
        authority.setFkitSuperGroup(group);
        authority.setPost(post);
        authority.setAuthorityLevel(authorityLevel);

        return this.authorityRepository.save(authority).toDTO();
    }
    protected List<GrantedAuthority> getGrantedAuthorities(ITUserDTO details) {
        List<MembershipDTO> memberships = this.membershipService.getMembershipsByUser(details);
        //  for (MembershipDTO membership : memberships) {
        //      AuthorityLevel authorityLevel = this.authorityLevelService
        //              .getAuthorityLevel(this.authorityLevelService.getAuthorityLevel(
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

    public List<AuthorityDTO> getAuthorityLevels(FKITSuperGroupDTO groupDTO, PostDTO postDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        Post post = this.postService.getPost(postDTO);
        List<Authority> authorities = this.authorityRepository.findByFkitSuperGroupAndPost(group, post);
        return authorities.stream().map((auth) -> auth.toDTO()).collect(Collectors.toList());
    }

    public void removeAuthority(FKITSuperGroupDTO groupDTO, PostDTO postDTO, AuthorityLevelDTO levelDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(groupDTO);
        Post post = this.postService.getPost(postDTO);
        AuthorityLevel level = this.authorityLevelService.getAuthorityLevel(levelDTO);
        Authority authority = this.authorityRepository.findByFkitSuperGroupAndPostAndAuthorityLevel(group, post, level)
                .orElseThrow(AuthorityDoesNotExistResponse::new);
        this.authorityRepository.delete(authority);
    }

    @Transactional
    public void removeAuthority(UUID id) {
        this.authorityRepository.deleteById(id);
    }

    public List<AuthorityLevelDTO> getAuthorities(List<MembershipDTO> memberships) {
        List<AuthorityLevelDTO> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            List<AuthorityDTO> authorities = this.getAuthorityLevels(
                    membership.getFkitGroupDTO().getSuperGroup(),
                    membership.getPost()
            );

            for(AuthorityDTO authority: authorities) {
                Calendar start = membership.getFkitGroupDTO().getBecomesActive();
                Calendar end = membership.getFkitGroupDTO().getBecomesInactive();
                Calendar now = Calendar.getInstance();
                if (now.after(start) && now.before(end)) {
                    authorityLevels.add(authority.getAuthorityLevel());
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

    public List<AuthorityDTO> getAuthoritiesWithLevel(UUID id) {
        return this.authorityRepository.findAllByAuthorityLevel(
                this.authorityLevelService.getAuthorityLevel(
                this.authorityLevelService.getAuthorityLevelDTO(id.toString()))).stream()
                .map(Authority::toDTO)
                .collect(Collectors.toList());
    }

    public AuthorityDTO getAuthority(UUID id) {
        return this.authorityRepository.findById(id)
                .orElseThrow(AuthorityDoesNotExistResponse::new).toDTO();
    }

    @Transactional
    public void removeAllAuthoritiesWithAuthorityLevel(AuthorityLevelDTO authorityLevelDTO) {
        List<AuthorityDTO> authorities = this.getAllAuthoritiesWithAuthorityLevel(authorityLevelDTO);
        authorities.forEach(a -> this.removeAuthority(a.getId()));
    }
}
