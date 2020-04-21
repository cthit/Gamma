package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;

import org.springframework.stereotype.Component;

@SuppressWarnings({"PMD.ExcessiveParameterList"})
@Component()
public final class BootstrapServiceHelper {

    private final ITUserService userService;

    private final FKITGroupService groupService;

    private final AuthorityLevelService authorityLevelService;

    private final PostService postService;

    private final MembershipService membershipService;

    private final AuthorityService authorityService;

    private final ITClientService itClientService;


    private final ApiKeyService apiKeyService;

    private final FKITSuperGroupService superGroupService;

    public BootstrapServiceHelper(ITUserService userService,
                                   FKITGroupService groupService,
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

    public FKITGroupService getGroupService() {
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
