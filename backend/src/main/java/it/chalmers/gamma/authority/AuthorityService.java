package it.chalmers.gamma.authority;

import it.chalmers.gamma.supergroup.FKITSuperGroup;
import it.chalmers.gamma.post.Post;
import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.supergroup.FKITSuperGroupDTO;
import it.chalmers.gamma.membership.MembershipDTO;

import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.ITUserDTO;
import it.chalmers.gamma.authority.response.AuthorityDoesNotExistResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import it.chalmers.gamma.supergroup.FKITSuperGroupService;
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
        FKITSuperGroup group = this.fkitSuperGroupService.fromDTO(groupDTO);
        AuthorityLevel authorityLevel = this.authorityLevelService.getAuthorityLevel(authorityLevelDTO);
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(
                group,
                post
        ).orElseGet(() -> {
            Authority auth = new Authority();
            AuthorityPK pk = new AuthorityPK();
            pk.setFkitGroup(group);
            pk.setPost(post);
            auth.setId(pk);
            return auth;
        });
        authority.setAuthorityLevel(authorityLevel);
        return this.authorityRepository.save(authority).toDTO();
    }

    public List<GrantedAuthority> getGrantedAuthorities(ITUserDTO details) {
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
        return this.authorityRepository.existsByInternalId(UUID.fromString(id));
    }

    public AuthorityDTO getAuthorityLevel(FKITSuperGroupDTO groupDTO, PostDTO postDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.fromDTO(groupDTO);
        Post post = this.postService.getPost(postDTO);
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post)
                .orElse(null);
        if (authority != null) {
            return authority.toDTO();
        }
        return null;
    }

    public void removeAuthority(FKITSuperGroupDTO groupDTO, PostDTO postDTO) {
        FKITSuperGroup group = this.fkitSuperGroupService.fromDTO(groupDTO);
        Post post = this.postService.getPost(postDTO);
        Authority authority = this.authorityRepository.findById_FkitSuperGroupAndId_Post(group, post)
                .orElseThrow(AuthorityDoesNotExistResponse::new);
        this.authorityRepository.delete(authority);
    }

    @Transactional
    public void removeAuthority(UUID id) {
        this.authorityRepository.deleteByInternalId(id);
    }

    public List<AuthorityLevelDTO> getAuthorities(List<MembershipDTO> memberships) {
        List<AuthorityLevelDTO> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            AuthorityDTO authority = this.getAuthorityLevel(
                    membership.getFkitGroupDTO().getSuperGroup(),
                    membership.getPost()
            );

            if (authority != null) {
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
        return this.authorityRepository.findByInternalId(id)
                .orElseThrow(AuthorityDoesNotExistResponse::new).toDTO();
    }

    @Transactional
    public void removeAllAuthoritiesWithAuthorityLevel(AuthorityLevelDTO authorityLevelDTO) {
        List<AuthorityDTO> authorities = this.getAllAuthoritiesWithAuthorityLevel(authorityLevelDTO);
        authorities.forEach(a -> this.removeAuthority(a.getId()));
    }
}
