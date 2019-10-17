package it.chalmers.delta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/img/**")
            .addResourceLocations("classpath:/static/img/");
    }


}
