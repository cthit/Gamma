package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.domain.Authorities;
import it.chalmers.gamma.domain.Authority;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.internal.authoritypost.service.AuthorityPostService;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupService;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserService;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.domain.Membership;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

    public Map<AuthorityLevelName, Authorities> getAuthorities() {
        Map<AuthorityLevelName, Authorities> authorityMap = new HashMap<>();

        this.authorityLevelService.getAll()
                .forEach(authorityLevelName -> authorityMap.put(
                        authorityLevelName,
                        new Authorities(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ));

        this.authorityPostService.getAll()
                .forEach(authorityPost ->
                        authorityMap.get(authorityPost.authorityLevelName()).postAuthorities().add(authorityPost)
                );

        this.authorityUserService.getAll()
                .forEach(authorityUser ->
                        authorityMap.get(authorityUser.authorityLevelName()).userAuthorities().add(authorityUser)
                );

        this.authoritySuperGroupService.getAll()
                .forEach(authoritySuperGroup ->
                        authorityMap.get(authoritySuperGroup.authorityLevelName()).superGroupAuthorities().add(authoritySuperGroup)
                );

        return authorityMap;
    }

    public List<AuthorityLevelName> getGrantedAuthorities(UserId userId) {
        return this.getGrantedAuthoritiesWithType(userId)
                .stream()
                .map(Authority::authority)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Authority> getGrantedAuthoritiesWithType(UserId userId) {
        List<Authority> authorities = new ArrayList<>();

        //User authorities
        this.authorityUserService.getByUser(userId)
                .stream()
                .map(authorityLevelName -> new Authority(authorityLevelName, AuthorityType.AUTHORITY))
                .forEach(authorities::add);

        List<Membership> memberships = this.membershipService.getMembershipsByUser(userId);

        memberships.forEach(membership -> {
            authorities.add(new Authority(new AuthorityLevelName(membership.group().name().get()), AuthorityType.GROUP));
            authorities.add(new Authority(new AuthorityLevelName(membership.group().superGroup().name().get()), AuthorityType.SUPERGROUP));
        });

        //Super groups authorities
        this.authoritySuperGroupService.getAll()
                .stream()
                .filter(authoritySuperGroup -> memberships
                        .stream()
                        .anyMatch(membership -> authoritySuperGroup.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authoritySuperGroup -> authorities.add(new Authority(authoritySuperGroup.authorityLevelName(), AuthorityType.AUTHORITY)));

        //Supergroup post authorities
        this.authorityPostService.getAll()
                .stream()
                .filter(authorityPost -> memberships
                        .stream()
                        .anyMatch(membership -> authorityPost.post().id().equals(membership.post().id())
                                && authorityPost.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authorityPost -> authorities.add(new Authority(authorityPost.authorityLevelName(), AuthorityType.AUTHORITY)));

        //remove duplicates
        return authorities
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public Authorities getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return new Authorities(
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
