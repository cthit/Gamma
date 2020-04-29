package it.chalmers.gamma.factories;

import it.chalmers.gamma.db.entity.Text;
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
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.service.WebsiteService;
import it.chalmers.gamma.service.WhitelistService;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockDatabaseGeneratorFactory {

    @Autowired
    private PostService postService;
    @Autowired
    private FKITSuperGroupService superGroupService;
    @Autowired
    private ActivationCodeService activationCodeService;
    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private ITClientService clientService;
    @Autowired
    private WhitelistService whitelistService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private AuthorityLevelService authorityLevelService;
    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MockITUserFactory mockITUserFactory;
    @Autowired
    private MockFKITGroupFactory mockFKITGroupFactory;


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

    public void generateNewMock() { // TODO Remove the clutter in this method
        if (!hasGeneratedMock) {
            this.user = this.mockITUserFactory.saveUser(this.mockITUserFactory.generateITUser("user", true));
            this.post = this.postService.addPost(new Text());
            this.superGroup =
                    this.superGroupService.createSuperGroup(RandomSuperGroupFactory.generateSuperGroup("supergroup"));
            this.group = this.mockFKITGroupFactory.saveGroup(mockFKITGroupFactory.generateActiveFKITGroup(
                    "group",
                    superGroup
            ));
            this.whitelist = this.whitelistService.addWhiteListedCID("user");
            this.activationCode = this.activationCodeService.saveActivationCode(
                    this.whitelist,
                    GenerationUtils.generateRandomString(10, GenerationUtils.CharacterTypes.NUMBERS));
            this.apiKeyService.createApiKey(new ApiKeyDTO("client",
                    new Text("client", "client")));
            this.apiKey = this.apiKeyService.getApiKeyDetails("client");
            this.authorityLevel = this.authorityLevelService.addAuthorityLevel("test");
            this.authority = this.authorityService.setAuthorityLevel(this.superGroup, this.post, this.authorityLevel);
            this.client = this.clientService.createITClient(
                    GenerationUtils.generateRandomString(20, GenerationUtils.CharacterTypes.LOWERCASE),
                    GenerationUtils.generateText(),
                    GenerationUtils.generateRandomString(20, GenerationUtils.CharacterTypes.LOWERCASE));
            this.website = this.websiteService.addPossibleWebsite(
                    GenerationUtils.generateRandomString(),
                    GenerationUtils.generateRandomString());
            this.membershipService.addUserToGroup(this.group, this.user, this.post, "test");
            hasGeneratedMock = true;
        }
    }

    public ITUserDTO getMockedUser() {
        return this.user;
    }
    // Make this prettier by making a list and looping
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
