package it.chalmers.gamma.authority;

import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelNotFoundException;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.service.MembershipFinder;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.post.PostFinder;
import it.chalmers.gamma.post.exception.PostNotFoundException;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupFinder;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.user.exception.UserNotFoundException;
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

    public List<AuthorityLevelDTO> getAuthorityLevels(List<MembershipDTO> memberships) {
        List<AuthorityLevelDTO> authorityLevels = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            Optional<AuthorityDTO> authority = this.getAuthority(
                    membership.getGroup().getSuperGroup(),
                    membership.getPost()
            );

            if (authority.isPresent()) {
                Calendar start = membership.getGroup().getBecomesActive();
                Calendar end = membership.getGroup().getBecomesInactive();
                Calendar now = Calendar.getInstance();
                if (now.after(start) && now.before(end)) {
                    authorityLevels.add(authority.get().getAuthorityLevel());
                }
            }
        }
        return authorityLevels;
    }

    public List<AuthorityDTO> getAuthorities() {
        return this.authorityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AuthorityDTO> getAuthoritiesWithAuthorityLevel(UUID authorityLevelId) {
        return this.authorityRepository.findAllByAuthorityLevelId(authorityLevelId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AuthorityDTO> getAuthoritiesWithAuthorityLevel(AuthorityLevelDTO authorityLevel) {
        return getAuthoritiesWithAuthorityLevel(authorityLevel.getId());
    }


    public List<GrantedAuthority> getGrantedAuthorities(UUID userId) throws UserNotFoundException {
        List<MembershipDTO> memberships = this.membershipFinder.getMembershipsByUser(userId);


        return new ArrayList<>(this.getAuthorityLevels(memberships));
    }

    public Optional<AuthorityDTO> getAuthority(SuperGroupDTO groupDTO, PostDTO postDTO) {
        Optional<Authority> authorityEntity = getAuthorityEntity(groupDTO, postDTO);
        Optional<AuthorityDTO> authority = Optional.empty();

        if(authorityEntity.isPresent()) {
            authority = authorityEntity.map(this::toDTO);
        }

        return authority;
    }

    protected Optional<Authority> getAuthorityEntity(SuperGroupDTO superGroup, PostDTO post) {
        return getAuthorityEntity(superGroup.getId(), post.getId());
    }

    protected Optional<Authority> getAuthorityEntity(UUID superGroupId, UUID postId) {
        return this.authorityRepository
                .findById_SuperGroupIdAndId_PostId(
                        superGroupId,
                        postId
                );
    }

    protected Optional<Authority> getAuthorityEntity(UUID id) {
        return this.authorityRepository.findById(id);
    }

    protected AuthorityDTO toDTO(Authority authority) {
        try {
            return new AuthorityDTO(
                    this.superGroupFinder.getSuperGroup(authority.getSuperGroupId()),
                    this.postFinder.getPost(authority.getPostId()),
                    authority.getId(),
                    this.authorityLevelFinder.getAuthorityLevel(authority.getAuthorityLevelId())
            );
        } catch (SuperGroupNotFoundException | PostNotFoundException | AuthorityLevelNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
