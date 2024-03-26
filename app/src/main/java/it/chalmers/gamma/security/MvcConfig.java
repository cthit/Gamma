package it.chalmers.gamma.security;

import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

  @Bean
  public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
    FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean =
        new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
    filterRegistrationBean.setUrlPatterns(List.of("/*"));
    return filterRegistrationBean;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");

    registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");

    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");

    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/txt/");
  }
}
