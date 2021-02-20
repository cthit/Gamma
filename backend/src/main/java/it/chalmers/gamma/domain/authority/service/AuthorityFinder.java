package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.domain.authority.data.Authority;
import it.chalmers.gamma.domain.authority.data.AuthorityDTO;
import it.chalmers.gamma.domain.authority.data.AuthorityPK;
import it.chalmers.gamma.domain.authority.data.AuthorityRepository;
import it.chalmers.gamma.domain.authority.exception.AuthorityNotFoundException;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.domain.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelNotFoundException;
import it.chalmers.gamma.domain.membership.data.MembershipDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthorityFinder {

    private final AuthorityRepository authorityRepository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final MembershipFinder membershipFinder;
    private final SuperGroupFinder superGroupFinder;
    private final PostFinder postFinder;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityFinder(AuthorityRepository authorityRepository,
                           AuthorityLevelRepository authorityLevelRepository,
                           MembershipFinder membershipFinder,
                           SuperGroupFinder superGroupFinder,
                           PostFinder postFinder,
                           AuthorityLevelFinder authorityLevelFinder) {
        this.authorityRepository = authorityRepository;
        this.authorityLevelRepository = authorityLevelRepository;
        this.membershipFinder = membershipFinder;
        this.superGroupFinder = superGroupFinder;
        this.postFinder = postFinder;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    public boolean authorityExists(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return this.authorityRepository.existsById(new AuthorityPK(
                superGroupId,
                postId,
                authorityLevelName.value
        ));
    }

    public List<AuthorityLevelName> getAuthorityLevels(List<MembershipDTO> memberships) {
        List<AuthorityLevelName> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            Calendar start = membership.getGroup().getBecomesActive();
            Calendar end = membership.getGroup().getBecomesInactive();
            Calendar now = Calendar.getInstance();
            if (now.after(start) && now.before(end)) {
                authorityLevels.addAll(
                        getAuthoritiesByMembership(membership)
                                .stream()
                                .map(AuthorityDTO::getAuthorityLevelName)
                                .collect(Collectors.toList())
                );
            }

        }
        return authorityLevels;
    }

    public List<AuthorityDTO> getAuthorities() {
        return this.authorityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AuthorityDTO> getAuthoritiesByMembership(MembershipDTO membership) {
        return this.authorityRepository.findAuthoritiesById_SuperGroupIdAndId_PostId(
                membership.getGroup().getSuperGroup().getId(), membership.getPost().getId()
        ).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AuthorityDTO> getAuthoritiesWithAuthorityLevel(AuthorityLevelName authorityLevelName) throws AuthorityLevelNotFoundException {
        if(!this.authorityLevelFinder.authorityLevelExists(authorityLevelName)) {
            throw new AuthorityLevelNotFoundException();
        }

        return this.authorityRepository.findAuthoritiesById_AuthorityLevelName(authorityLevelName.value)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AuthorityLevelName> getGrantedAuthorities(UserId userId) throws UserNotFoundException {
        return this.getAuthorityLevels(this.membershipFinder.getMembershipsByUser(userId));
    }

    protected AuthorityDTO toDTO(Authority authority) {
        try {
            AuthorityPK pk = authority.getId();

            return new AuthorityDTO(
                    this.superGroupFinder.getSuperGroup(pk.getSuperGroupId()),
                    this.postFinder.getPost(pk.getPostId()),
                    new AuthorityLevelName(pk.getAuthorityLevelName())
            );
        } catch (SuperGroupNotFoundException | PostNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
