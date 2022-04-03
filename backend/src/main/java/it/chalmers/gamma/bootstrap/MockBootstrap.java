package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class MockBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBootstrap.class);

    private final ResourceLoader resourceLoader;

    public MockBootstrap(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public BootstrapSettings loadBootstrapSettings(@Value("${application.admin-setup}") boolean adminSetup,
                                                   @Value("${application.mocking}") boolean mocking) {
        return new BootstrapSettings(adminSetup, mocking);
    }

    @Bean
    public MockData mockData(BootstrapSettings bootstrapSettings) {
        if (!bootstrapSettings.mocking()) {
            LOGGER.info("Not running mock...");
            return MockData.empty();
        }

        Resource resource = this.resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resource.getInputStream(), MockData.class);
        } catch (IOException e) {
            LOGGER.error("Error when trying to read mock.json", e);
            return MockData.empty();
        }
    }

}
