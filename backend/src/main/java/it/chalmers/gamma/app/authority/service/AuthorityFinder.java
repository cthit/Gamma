package it.chalmers.gamma.app.authority.service;

import it.chalmers.gamma.app.domain.Authorities;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.app.authoritylevel.service.GrantedAuthorityImpl;
import it.chalmers.gamma.app.authoritypost.service.AuthorityPostService;
import it.chalmers.gamma.app.authoritysupergroup.service.AuthoritySuperGroupService;
import it.chalmers.gamma.app.authorityuser.service.AuthorityUserService;
import it.chalmers.gamma.app.domain.Membership;
import it.chalmers.gamma.app.membership.service.MembershipService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorityFinder {

    private final AuthorityPostService authorityPostService;
    private final AuthorityUserService authorityUserService;
    private final AuthoritySuperGroupService authoritySuperGroupService;
    private final AuthorityLevelService authorityLevelService;
    private final MembershipService membershipService;

    public AuthorityFinder(AuthorityPostService authorityPostService,
                           AuthorityUserService authorityUserService,
                           AuthoritySuperGroupService authoritySuperGroupService,
                           AuthorityLevelService authorityLevelService,
                           MembershipService membershipService) {
        this.authorityPostService = authorityPostService;
        this.authorityUserService = authorityUserService;
        this.authoritySuperGroupService = authoritySuperGroupService;
        this.authorityLevelService = authorityLevelService;
        this.membershipService = membershipService;
    }

    public List<Authorities> getAuthorities() {
        Map<AuthorityLevelName, Authorities> authorityMap = new HashMap<>();

        this.authorityLevelService.getAll()
                .forEach(authorityLevelName -> authorityMap.put(
                        authorityLevelName,
                        new Authorities(
                                authorityLevelName,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                ));

        this.authorityPostService.getAll()
                .forEach(authorityPost ->
                        authorityMap.get(authorityPost.authorityLevelName()).posts().add(new Authorities.SuperGroupPost(authorityPost.superGroup(), authorityPost.post()))
                );

        this.authorityUserService.getAll()
                .forEach(authorityUser ->
                        authorityMap.get(authorityUser.authorityLevelName()).users().add(authorityUser.user())
                );

        this.authoritySuperGroupService.getAll()
                .forEach(authoritySuperGroup ->
                        authorityMap.get(authoritySuperGroup.authorityLevelName()).superGroups().add(authoritySuperGroup.superGroup())
                );

        return authorityMap.values().stream().toList();
    }

    public List<GrantedAuthorityImpl> getGrantedAuthorities(UserId userId) {
        List<GrantedAuthorityImpl> authorities = new ArrayList<>();

        //User restrictions
        this.authorityUserService.getByUser(userId)
                .stream()
                .map(authorityLevelName -> new GrantedAuthorityImpl(authorityLevelName, AuthorityType.AUTHORITY))
                .forEach(authorities::add);

        List<Membership> memberships = this.membershipService.getMembershipsByUser(userId);

        memberships.forEach(membership -> {
            authorities.add(new GrantedAuthorityImpl(AuthorityLevelName.valueOf(membership.group().name().get()), AuthorityType.GROUP));
            authorities.add(new GrantedAuthorityImpl(AuthorityLevelName.valueOf(membership.group().superGroup().name().get()), AuthorityType.SUPERGROUP));
        });

        //Super groups restrictions
        this.authoritySuperGroupService.getAll()
                .stream()
                .filter(authoritySuperGroup -> memberships
                        .stream()
                        .anyMatch(membership -> authoritySuperGroup.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authoritySuperGroup -> authorities.add(new GrantedAuthorityImpl(authoritySuperGroup.authorityLevelName(), AuthorityType.AUTHORITY)));

        //Supergroup post restrictions
        this.authorityPostService.getAll()
                .stream()
                .filter(authorityPost -> memberships
                        .stream()
                        .anyMatch(membership -> authorityPost.post().id().equals(membership.post().id())
                                && authorityPost.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authorityPost -> authorities.add(new GrantedAuthorityImpl(authorityPost.authorityLevelName(), AuthorityType.AUTHORITY)));

        //remove duplicates
        return authorities
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public Authorities getByAuthorityLevel(AuthorityLevelName authorityLevelName) throws AuthorityLevelService.AuthorityLevelNotFoundException {
        if (!this.authorityLevelService.exists(authorityLevelName)) {
            throw new AuthorityLevelService.AuthorityLevelNotFoundException();
        }

        return new Authorities(
                authorityLevelName,
                this.authorityPostService.getByAuthorityLevel(authorityLevelName),
                this.authoritySuperGroupService.getByAuthorityLevel(authorityLevelName),
                this.authorityUserService.getByAuthorityLevel(authorityLevelName)
        );
    }

    public boolean authorityLevelUsed(AuthorityLevelName name) {
        return this.authorityPostService.existsBy(name)
                || this.authorityUserService.existsBy(name)
                || this.authoritySuperGroupService.existsBy(name);
    }

}
