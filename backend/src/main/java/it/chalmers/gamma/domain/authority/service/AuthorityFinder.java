package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.domain.membership.service.MembershipDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.user.service.UserId;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthorityFinder implements GetAllEntities<AuthorityDTO> {

    private final AuthorityRepository authorityRepository;
    private final MembershipFinder membershipFinder;
    private final SuperGroupFinder superGroupFinder;
    private final PostFinder postFinder;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityFinder(AuthorityRepository authorityRepository,
                           MembershipFinder membershipFinder,
                           SuperGroupFinder superGroupFinder,
                           PostFinder postFinder,
                           AuthorityLevelFinder authorityLevelFinder) {
        this.authorityRepository = authorityRepository;
        this.membershipFinder = membershipFinder;
        this.superGroupFinder = superGroupFinder;
        this.postFinder = postFinder;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    public List<AuthorityDTO> getAll() {
        return this.authorityRepository
                .findAll()
                .stream()
                .map(Authority::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<AuthorityLevelName> getAuthorityLevels(List<MembershipDTO> memberships) {
        List<AuthorityLevelName> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            Calendar start = membership.group().becomesActive();
            Calendar end = membership.group().becomesInactive();
            Calendar now = Calendar.getInstance();
            if (now.after(start) && now.before(end)) {
                authorityLevels.addAll(
                        getByMembership(membership)
                                .stream()
                                .map(AuthorityDTO::authorityLevelName)
                                .collect(Collectors.toList())
                );
            }

        }
        return authorityLevels;
    }

    public List<AuthorityDTO> getByAuthorityLevel(AuthorityLevelName authorityLevelName) throws EntityNotFoundException {
        if(!this.authorityLevelFinder.authorityLevelExists(authorityLevelName)) {
            throw new EntityNotFoundException();
        }

        return this.authorityRepository.findAuthoritiesById_AuthorityLevelName(authorityLevelName.value)
                .stream()
                .map(Authority::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<AuthorityLevelName> getGrantedAuthorities(UserId userId) throws EntityNotFoundException {
        return this.getAuthorityLevels(this.membershipFinder.getMembershipsByUser(userId));
    }

    protected AuthorityDTO fromShallow(AuthorityShallowDTO authority) {
        try {
            return new AuthorityDTO(
                    this.superGroupFinder.get(authority.superGroupId()),
                    this.postFinder.get(authority.postId()),
                    authority.authorityLevelName()
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<AuthorityDTO> getByMembership(MembershipDTO membership) {
        return this.authorityRepository.findAuthoritiesById_SuperGroupIdAndId_PostId(
                membership.group().superGroup().id(), membership.post().id()
        )
                .stream()
                .map(Authority::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }
}
