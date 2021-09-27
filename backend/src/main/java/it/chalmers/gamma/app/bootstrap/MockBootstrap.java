package it.chalmers.gamma.app.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
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

    //TODO: Only load if mocking
    @Bean
    public MockData mockData() {
        Resource resource = this.resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resource.getInputStream(), MockData.class);
        } catch (IOException e) {
            LOGGER.error("Error when trying to read mock.json", e);
            return new MockData(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
    }

}
