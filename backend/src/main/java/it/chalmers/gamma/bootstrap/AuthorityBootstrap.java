package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.internal.authoritypost.service.AuthorityPostService;
import it.chalmers.gamma.internal.authoritypost.service.AuthorityPostShallowDTO;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupService;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupShallowDTO;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserService;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserShallowDTO;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("groupBootstrap")
@Component
public class AuthorityBootstrap {

    private static final Logger LOGGER = Logger.getLogger(AuthorityBootstrap.class);

    private final MockData mockData;
    private final AuthorityLevelService authorityLevelService;
    private final AuthorityPostService authorityPostService;
    private final AuthorityUserService authorityUserService;
    private final AuthoritySuperGroupService authoritySuperGroupService;
    private final boolean mocking;

    public AuthorityBootstrap(MockData mockData,
                              AuthorityLevelService authorityLevelService,
                              AuthorityPostService authorityPostService,
                              AuthorityUserService authorityUserService,
                              AuthoritySuperGroupService authoritySuperGroupService,
                              @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.authorityLevelService = authorityLevelService;
        this.authorityPostService = authorityPostService;
        this.authorityUserService = authorityUserService;
        this.authoritySuperGroupService = authoritySuperGroupService;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createAuthorities() {
        //!= 1 implies that admin isn't the only authority level
        if (!this.mocking || this.authorityLevelService.getAll().size() != 1) {
            return;
        }

        LOGGER.info("========== AUTHORITY BOOTSTRAP ==========");

        this.mockData.users().forEach(mockUser -> {
            if (mockUser.authorities() != null) {
                mockUser.authorities().forEach(authorityLevelName -> {
                    try {
                        this.authorityLevelService.create(authorityLevelName);
                    } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException ignored) { }

                    try {
                        this.authorityUserService.create(new AuthorityUserShallowDTO(mockUser.id(), authorityLevelName));
                    } catch (AuthorityUserService.AuthorityUserNotFoundException ignored) { }
                });
            }
        });

        this.mockData.superGroups().forEach(mockSuperGroup -> {
            if (mockSuperGroup.authorities() != null) {
                mockSuperGroup.authorities().forEach(authorityLevelName -> {
                    try {
                        this.authorityLevelService.create(authorityLevelName);
                    } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException ignored) {
                    }

                    try {
                        this.authoritySuperGroupService.create(new AuthoritySuperGroupShallowDTO(mockSuperGroup.id(), authorityLevelName));
                    } catch (AuthoritySuperGroupService.AuthoritySuperGroupAlreadyExistsException ignored) {
                    }
                });
            }
        });

        this.mockData.postAuthorities().forEach(mockPostAuthority -> {
            try {
                this.authorityLevelService.create(mockPostAuthority.name());
            } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException ignored) { }

            try {
                this.authorityPostService.create(new AuthorityPostShallowDTO(
                        mockPostAuthority.superGroupId(),
                        mockPostAuthority.postId(),
                        mockPostAuthority.name())
                );
            } catch (AuthorityPostService.AuthorityPostNotFoundException ignored) { }
        });

        LOGGER.info("========== AUTHORITY BOOTSTRAP ==========");
    }

}
