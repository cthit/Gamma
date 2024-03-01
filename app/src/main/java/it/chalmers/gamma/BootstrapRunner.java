package it.chalmers.gamma;

import it.chalmers.gamma.bootstrap.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapRunner {

  @Bean
  public CommandLineRunner runBootStrap(
      ApiKeyBootstrap apiKeyBootstrap,
      EnsureAnAdminUserBootstrap ensureAnAdminUserBootstrap,
      EnsureSettingsBootstrap ensureSettingsBootstrap,
      GroupBootstrap groupBootstrap,
      MiscBootstrap miscBootstrap,
      PostBootstrap postBootstrap,
      SuperGroupBootstrap superGroupBootstrap,
      UserBootstrap userBootstrap) {
    return (args) -> {
      try {
        SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.getContext().setAuthentication(new BootstrapAuthenticated());

        miscBootstrap.runImageBootstrap();

        ensureSettingsBootstrap.ensureAppSettings();

        ensureAnAdminUserBootstrap.ensureAnAdminUser();

        userBootstrap.createUsers();
        postBootstrap.createPosts();
        superGroupBootstrap.createSuperGroups();
        groupBootstrap.createGroups();

        apiKeyBootstrap.ensureApiKeys();

        SecurityContextHolder.clearContext();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    };
  }
}
