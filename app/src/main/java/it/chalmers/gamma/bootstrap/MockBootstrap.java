package it.chalmers.gamma.bootstrap;

import java.io.IOException;
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
      @Value("${application.production}") boolean production) {
    return new BootstrapSettings(adminSetup, !production);
  }

  @Bean
  public MockData mockData(
      BootstrapSettings bootstrapSettings,
      @Value("${application.mock-data-resource:classpath:/mock/mock.json}") String mockDataResource) {
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
}
