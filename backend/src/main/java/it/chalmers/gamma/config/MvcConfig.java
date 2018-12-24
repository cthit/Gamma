package it.chalmers.gamma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
//@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
/*
    @Value("${application.allowed-origin}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(this.allowedOrigins) //http://localhost:3000 or account.chalmers.it
                .allowedMethods("GET", "POST", "UPDATE", "PUT", "DELETE");
    }*/

}
