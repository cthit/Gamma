package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.domain.Authorities;
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

@Service
public class AuthorityFinder {

    private final AuthorityPostService authorityPostService;
    private final AuthorityUserService authorityUserService;
    private final AuthoritySuperGroupService authoritySuperGroupService;
    private final AuthorityLevelService authorityLevelService;
    private final SuperGroupService superGroupService;
    private final GroupService groupService;
    private final MembershipService membershipService;

    public AuthorityFinder(AuthorityPostService authorityPostService,
                           AuthorityUserService authorityUserService,
                           AuthoritySuperGroupService authoritySuperGroupService,
                           AuthorityLevelService authorityLevelService,
                           SuperGroupService superGroupService,
                           GroupService groupService,
                           MembershipService membershipService) {
        this.authorityPostService = authorityPostService;
        this.authorityUserService = authorityUserService;
        this.authoritySuperGroupService = authoritySuperGroupService;
        this.authorityLevelService = authorityLevelService;
        this.superGroupService = superGroupService;
        this.groupService = groupService;
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
        Set<AuthorityLevelName> authorities = new HashSet<>();

        //User authorities
        authorities.addAll(this.authorityUserService.getByUser(userId));

        List<Membership> memberships = this.membershipService.getMembershipsByUser(userId);

        //Super groups authorities
        this.authoritySuperGroupService.getAll()
                .stream()
                .filter(authoritySuperGroup -> memberships
                        .stream()
                        .anyMatch(membership -> authoritySuperGroup.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authoritySuperGroup -> authorities.add(authoritySuperGroup.authorityLevelName()));

        //Supergroup post authorities
        this.authorityPostService.getAll()
                .stream()
                .filter(authorityPost -> memberships
                        .stream()
                        .anyMatch(membership -> authorityPost.post().id().equals(membership.post().id())
                                && authorityPost.superGroup().id().equals(membership.group().superGroup().id())))
                .forEach(authorityPost -> authorities.add(authorityPost.authorityLevelName()));

        return new ArrayList<>(authorities);
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
