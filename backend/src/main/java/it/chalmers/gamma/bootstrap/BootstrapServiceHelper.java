package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.apikey.ApiKeyService;
import it.chalmers.gamma.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.authority.AuthorityService;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.supergroup.SuperGroupService;
import it.chalmers.gamma.client.ITClientService;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.post.PostService;

import org.springframework.stereotype.Component;

@SuppressWarnings({"PMD.ExcessiveParameterList"})
@Component()
public final class BootstrapServiceHelper {

    private final UserFinder userFinder;
    private final UserService userService;
    private final GroupService groupService;
    private final AuthorityLevelService authorityLevelService;
    private final PostService postService;
    private final MembershipService membershipService;
    private final AuthorityService authorityService;
    private final ITClientService itClientService;
    private final ApiKeyService apiKeyService;
    private final SuperGroupService superGroupService;

    public BootstrapServiceHelper(UserFinder userFinder,
                                  UserService userService,
                                  GroupService groupService,
                                  AuthorityLevelService authorityLevelService,
                                  PostService postService,
                                  MembershipService membershipService,
                                  AuthorityService authorityService,
                                  ITClientService itClientService,
                                  ApiKeyService apiKeyService,
                                  SuperGroupService superGroupService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.itClientService = itClientService;
        this.apiKeyService = apiKeyService;
        this.superGroupService = superGroupService;
    }

    public UserService getUserService() {
        return this.userService;
    }

    public GroupService getGroupService() {
        return this.groupService;
    }

    public AuthorityLevelService getAuthorityLevelService() {
        return this.authorityLevelService;
    }

    public PostService getPostService() {
        return this.postService;
    }

    public MembershipService getMembershipService() {
        return this.membershipService;
    }

    public AuthorityService getAuthorityService() {
        return this.authorityService;
    }

    public ITClientService getItClientService() {
        return this.itClientService;
    }

    public ApiKeyService getApiKeyService() {
        return this.apiKeyService;
    }

    public SuperGroupService getSuperGroupService() {
        return this.superGroupService;
    }

    public UserFinder getUserFinder() {
        return userFinder;
    }
}
