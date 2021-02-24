package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.domain.EntityExists;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.GetAllEntities;
import it.chalmers.gamma.domain.authority.data.db.Authority;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityDTO;
import it.chalmers.gamma.domain.authority.data.db.AuthorityPK;
import it.chalmers.gamma.domain.authority.data.db.AuthorityRepository;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityShallowDTO;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.domain.membership.data.dto.MembershipDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.user.UserId;
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
            Calendar start = membership.getGroup().getBecomesActive();
            Calendar end = membership.getGroup().getBecomesInactive();
            Calendar now = Calendar.getInstance();
            if (now.after(start) && now.before(end)) {
                authorityLevels.addAll(
                        getByMembership(membership)
                                .stream()
                                .map(AuthorityDTO::getAuthorityLevelName)
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
                    this.superGroupFinder.get(authority.getSuperGroupId()),
                    this.postFinder.get(authority.getPostId()),
                    authority.getAuthorityLevelName()
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<AuthorityDTO> getByMembership(MembershipDTO membership) {
        return this.authorityRepository.findAuthoritiesById_SuperGroupIdAndId_PostId(
                membership.getGroup().getSuperGroup().getId(), membership.getPost().getId()
        )
                .stream()
                .map(Authority::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }
}
