package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("groupBootstrap")
@Component
public class AuthorityLevelBootstrap {

    private static final Logger LOGGER = Logger.getLogger(AuthorityLevelBootstrap.class);

    private final MockData mockData;
    private final AuthorityLevelFacade authorityLevelFacade;
    private final boolean mocking;

    public AuthorityLevelBootstrap(MockData mockData,
                                   AuthorityLevelFacade authorityLevelFacade,
                                   @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.authorityLevelFacade = authorityLevelFacade;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createAuthorities() {
        //!= 1 implies that admin isn't the only authority level
        if (!this.mocking || this.authorityLevelFacade.getAll().size() != 1) {
            return;
        }

        LOGGER.info("========== AUTHORITY BOOTSTRAP ==========");

        this.mockData.users().forEach(mockUser -> {
            if (mockUser.authorities() != null) {
                mockUser.authorities().forEach(authorityLevelName -> {
                    this.authorityLevelFacade.create(authorityLevelName);
                    this.authorityLevelFacade.addUserToAuthorityLevel(authorityLevelName, mockUser.id());
                });
            }
        });

        this.mockData.superGroups().forEach(mockSuperGroup -> {
            if (mockSuperGroup.authorities() != null) {
                mockSuperGroup.authorities().forEach(authorityLevelName -> {
                    this.authorityLevelFacade.create(authorityLevelName);
                    this.authorityLevelFacade.addSuperGroupToAuthorityLevel(authorityLevelName, mockSuperGroup.id());
                });
            }
        });

        this.mockData.postAuthorities().forEach(mockPostAuthority -> {
            this.authorityLevelFacade.create(mockPostAuthority.name());
            this.authorityLevelFacade.addSuperGroupPostToAuthorityLevel(
                    mockPostAuthority.name(),
                    mockPostAuthority.superGroupId(),
                    mockPostAuthority.postId()
            );
        });

        LOGGER.info("========== AUTHORITY BOOTSTRAP ==========");
    }

}
