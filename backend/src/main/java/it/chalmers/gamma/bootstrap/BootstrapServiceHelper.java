package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.apikey.ApiKeyService;
import it.chalmers.gamma.authority.AuthorityLevelService;
import it.chalmers.gamma.authority.AuthorityService;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.supergroup.FKITSuperGroupService;
import it.chalmers.gamma.client.ITClientService;
import it.chalmers.gamma.user.ITUserService;
import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.post.PostService;

import org.springframework.stereotype.Component;

@SuppressWarnings({"PMD.ExcessiveParameterList"})
@Component()
public final class BootstrapServiceHelper {

    private final ITUserService userService;
    private final GroupService groupService;
    private final AuthorityLevelService authorityLevelService;
    private final PostService postService;
    private final MembershipService membershipService;
    private final AuthorityService authorityService;
    private final ITClientService itClientService;
    private final ApiKeyService apiKeyService;
    private final FKITSuperGroupService superGroupService;

    public BootstrapServiceHelper(ITUserService userService,
                                  GroupService groupService,
                                  AuthorityLevelService authorityLevelService,
                                  PostService postService,
                                  MembershipService membershipService,
                                  AuthorityService authorityService,
                                  ITClientService itClientService,
                                  ApiKeyService apiKeyService,
                                  FKITSuperGroupService superGroupService) {
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

    public ITUserService getUserService() {
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

    public FKITSuperGroupService getSuperGroupService() {
        return this.superGroupService;
    }
}
