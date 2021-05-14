package it.chalmers.gamma.internal.authority.service.post;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.internal.post.service.PostFinder;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.internal.user.service.UserId;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthorityPostFinder implements GetAllEntities<AuthorityPostDTO> {

    private final AuthorityPostRepository authorityPostRepository;
    private final MembershipFinder membershipFinder;
    private final SuperGroupFinder superGroupFinder;
    private final PostFinder postFinder;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityPostFinder(AuthorityPostRepository authorityPostRepository,
                               MembershipFinder membershipFinder,
                               SuperGroupFinder superGroupFinder,
                               PostFinder postFinder,
                               AuthorityLevelFinder authorityLevelFinder) {
        this.authorityPostRepository = authorityPostRepository;
        this.membershipFinder = membershipFinder;
        this.superGroupFinder = superGroupFinder;
        this.postFinder = postFinder;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    public List<AuthorityPostDTO> getAll() {
        return this.authorityPostRepository
                .findAll()
                .stream()
                .map(AuthorityPost::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    //todo add user and all for supergroup
    public List<AuthorityLevelName> getAuthorityLevels(List<MembershipDTO> memberships) {
        List<AuthorityLevelName> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            authorityLevels.addAll(
                    getByMembership(membership)
                            .stream()
                            .map(AuthorityPostDTO::authorityLevelName)
                            .collect(Collectors.toList())
            );
        }
        return authorityLevels;
    }

    public List<AuthorityPostDTO> getByAuthorityLevel(AuthorityLevelName authorityLevelName) throws EntityNotFoundException {
        if(!this.authorityLevelFinder.authorityLevelExists(authorityLevelName)) {
            throw new EntityNotFoundException();
        }

        return this.authorityPostRepository.findAuthoritiesById_AuthorityLevelName(authorityLevelName)
                .stream()
                .map(AuthorityPost::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<AuthorityLevelName> getGrantedAuthorities(UserId userId) throws EntityNotFoundException {
        return this.getAuthorityLevels(this.membershipFinder.getMembershipsByUser(userId));
    }

    protected AuthorityPostDTO fromShallow(AuthorityPostShallowDTO authority) {
        try {
            return new AuthorityPostDTO(
                    this.superGroupFinder.get(authority.superGroupId()),
                    this.postFinder.get(authority.postId()),
                    authority.authorityLevelName()
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<AuthorityPostDTO> getByMembership(MembershipDTO membership) {
        return this.authorityPostRepository.findAuthoritiesById_SuperGroupIdAndId_PostId(
                membership.group().superGroup().id(), membership.post().id()
        )
                .stream()
                .map(AuthorityPost::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }
}
