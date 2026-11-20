package it.chalmers.gamma.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MockBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockBootstrap.class);

  private final ResourceLoader resourceLoader;

  public MockBootstrap(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Bean
  public BootstrapSettings loadBootstrapSettings(
      @Value("${application.admin-setup}") boolean adminSetup,
      @Value("${application.production}") boolean production,
      @Value("${application.mocking:false}") boolean isMocking,
      @Value("${application.mock-client-redirect-url}") String mockClientRedirectUrl,
      @Value("${application.mock-client-email-scope}") boolean mockClientEmailScope,
      @Value("${application.mock-client-authority-name}") String mockClientAuthorityName,
      @Value("${application.mock-client-authority-super-groups}")
          List<UUID> mockClientAuthoritySuperGroups,
      @Value("${application.mock-client-authority-super-group-posts}")
          String mockClientAuthoritySuperGroupPosts,
      @Value("${application.mock-client-authority-users}") List<UUID> mockClientAuthorityUsers) {
    MockData.MockClientAuthority clientAuthority =
        new MockData.MockClientAuthority(
            mockClientAuthorityName,
            mockClientAuthoritySuperGroups,
            parseMockSuperGroupPosts(mockClientAuthoritySuperGroupPosts),
            mockClientAuthorityUsers);

    return new BootstrapSettings(
        adminSetup,
        !production || isMocking,
        mockClientRedirectUrl,
        mockClientEmailScope,
        clientAuthority);
  }

  @Bean
  public MockData mockData(
      BootstrapSettings bootstrapSettings,
      @Value("${application.mock-data-resource:classpath:/mock/mock.json}")
          String mockDataResource) {
    if (!bootstrapSettings.mocking()) {
      LOGGER.info("Not running mock...");
      return MockData.empty();
    }

    Resource resource = this.resourceLoader.getResource(mockDataResource);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(resource.getInputStream(), MockData.class);
    } catch (IOException e) {
      LOGGER.error("Error when trying to read mock.json", e);
      return MockData.empty();
    }
  }

  private List<MockData.MockSuperGroupPost> parseMockSuperGroupPosts(String source) {
    List<MockData.MockSuperGroupPost> posts = new ArrayList<>();
    if (source == "") {
      return posts;
    }

    String[] parts = source.split(",");
    for (String part : parts) {
      String[] subparts = part.split(":");
      UUID superGroupId = UUID.fromString(subparts[0]);
      UUID postId = UUID.fromString(subparts[1]);
      posts.add(new MockData.MockSuperGroupPost(superGroupId, postId));
    }
    return posts;
  }
}
