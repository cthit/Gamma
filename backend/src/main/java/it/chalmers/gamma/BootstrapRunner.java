package it.chalmers.gamma;


import it.chalmers.gamma.bootstrap.AdminAuthorityLevelBootstrap;
import it.chalmers.gamma.bootstrap.ApiKeyBootstrap;
import it.chalmers.gamma.bootstrap.AuthorityLevelBootstrap;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.bootstrap.ClientBootstrap;
import it.chalmers.gamma.bootstrap.EnsureAnAdminUserBootstrap;
import it.chalmers.gamma.bootstrap.EnsureSettingsBootstrap;
import it.chalmers.gamma.bootstrap.GroupBootstrap;
import it.chalmers.gamma.bootstrap.MiscBootstrap;
import it.chalmers.gamma.bootstrap.PostBootstrap;
import it.chalmers.gamma.bootstrap.SuperGroupBootstrap;
import it.chalmers.gamma.bootstrap.UserBootstrap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapRunner {

    @Bean
    public CommandLineRunner runBootStrap(AdminAuthorityLevelBootstrap adminAuthorityLevelBootstrap,
                                          ApiKeyBootstrap apiKeyBootstrap,
                                          AuthorityLevelBootstrap authorityLevelBootstrap,
                                          ClientBootstrap clientBootstrap,
                                          EnsureAnAdminUserBootstrap ensureAnAdminUserBootstrap,
                                          EnsureSettingsBootstrap ensureSettingsBootstrap,
                                          GroupBootstrap groupBootstrap,
                                          MiscBootstrap miscBootstrap,
                                          PostBootstrap postBootstrap,
                                          SuperGroupBootstrap superGroupBootstrap,
                                          UserBootstrap userBootstrap) {
        return (args) -> {
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(new BootstrapAuthenticated());

            miscBootstrap.runImageBootstrap();

            ensureSettingsBootstrap.ensureAppSettings();

            adminAuthorityLevelBootstrap.ensureAdminAuthorityLevel();
            ensureAnAdminUserBootstrap.ensureAnAdminUser();

            userBootstrap.createUsers();
            postBootstrap.createPosts();
            superGroupBootstrap.createSuperGroups();
            groupBootstrap.createGroups();
            authorityLevelBootstrap.createAuthorities();

            clientBootstrap.runOauthClient();
            apiKeyBootstrap.ensureApiKeys();

            SecurityContextHolder.clearContext();
        };
    }

}
