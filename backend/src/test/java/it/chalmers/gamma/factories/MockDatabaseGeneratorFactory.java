package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"PMD.TooManyFields", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public class MockDatabaseGeneratorFactory {

    @Autowired
    private MockITUserFactory mockITUserFactory;
    @Autowired
    private MockFKITGroupFactory mockFKITGroupFactory;
    @Autowired
    private MockITClientFactory mockITClientFactory;
    @Autowired
    private MockActivationCodeFactory mockActivationCodeFactory;
    @Autowired
    private MockApiKeyFactory mockApiKeyFactory;
    @Autowired
    private MockAuthorityLevelFactory mockAuthorityLevelFactory;
    @Autowired
    private MockSuperGroupFactory mockSuperGroupFactory;
    @Autowired
    private MockMembershipFactory mockMembershipFactory;
    @Autowired
    private MockWebsiteFactory mockWebsiteFactory;
    @Autowired
    private MockAuthorityFactory mockAuthorityFactory;
    @Autowired
    private MockPostFactory mockPostFactory;
    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;



    private ITUserDTO user;
    private PostDTO post;
    private FKITGroupDTO group;
    private FKITSuperGroupDTO superGroup;
    private ActivationCodeDTO activationCode;
    private ApiKeyDTO apiKey;
    private AuthorityLevelDTO authorityLevel;
    private AuthorityDTO authority;
    private ITClientDTO client;
    private WhitelistDTO whitelist;
    private WebsiteDTO website;

    private static boolean hasGeneratedMock;

    public void populateMockDatabase() { // TODO Remove the clutter in this method
        if (!hasGeneratedMock) {
            this.user = this.mockITUserFactory.saveUser(this.mockITUserFactory.generateITUser("user", true));
            this.post = this.mockPostFactory.savePost(this.mockPostFactory.generatePost());
            this.superGroup = this.mockSuperGroupFactory.saveSuperGroup(
                    this.mockSuperGroupFactory.generateSuperGroup("group")
            );
            this.group = this.mockFKITGroupFactory.saveGroup(this.mockFKITGroupFactory.generateActiveFKITGroup(
                    "group",
                    this.superGroup
            ));
            this.whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
            this.activationCode = this.mockActivationCodeFactory.saveActivationCode(this.whitelist);
            this.apiKey = this.mockApiKeyFactory.saveApiKey(this.mockApiKeyFactory.generateApiKey());

            this.authorityLevel = this.mockAuthorityLevelFactory.saveAuthorityLevel(
                    this.mockAuthorityLevelFactory.generateAuthorityLevel());
            this.authority = this.mockAuthorityFactory.saveAuthority(
                    this.mockAuthorityFactory.generateAuthority(
                            this.superGroup,
                            this.post,
                            this.authorityLevel
                            ));
            this.client = this.mockITClientFactory.saveClient(this.mockITClientFactory.generateClient(
                    GenerationUtils.generateRandomString()
            ));
            this.website = this.mockWebsiteFactory.saveWebsite(this.mockWebsiteFactory.generateWebsite());
            this.mockMembershipFactory.saveMembership(
                    this.mockMembershipFactory.generateMembership(
                            this.post,
                            this.group,
                            this.user));
            hasGeneratedMock = true;
        }
    }

    // Make this prettier by making a list and looping, or by using interfaces
    public UUID getMockedUUID(Class c) {
        if (this.user.getClass() == c) {
            return this.user.getId();
        }
        if (this.post.getClass() == c) {
            return this.post.getId();
        }
        if (this.group.getClass() == c) {
            return this.group.getId();
        }
        if (this.superGroup.getClass() == c) {
            return this.superGroup.getId();
        }
        if (this.activationCode.getClass() == c) {
            return this.activationCode.getId();
        }
        if (this.apiKey.getClass() == c) {
            return this.apiKey.getId();
        }
        if (this.authority.getClass() == c) {
            return this.authority.getInternalID();
        }
        if (this.client.getClass() == c) {
            return this.client.getId();
        }
        if (this.website.getClass() == c) {
            return this.website.getId();
        }
        if (this.authorityLevel.getClass() == c) {
            return this.authorityLevel.getId();
        }
        if (this.whitelist.getClass() == c) {
            return this.whitelist.getId();
        }
        return null;
    }

}
