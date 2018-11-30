package it.chalmers.gamma.config;

import com.google.api.client.util.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Value(value = "${application.allowed-origin}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println(allowedOrigins);
        registry.addMapping("/**")
            .allowedOrigins(this.allowedOrigins) //http://localhost:3000 or account.chalmers.it
            .allowedMethods("GET", "POST", "UPDATE", "PUT", "DELETE");
    }
}
