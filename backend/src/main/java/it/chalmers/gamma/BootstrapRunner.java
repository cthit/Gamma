package it.chalmers.gamma;


import it.chalmers.gamma.app.bootstrap.AdminAuthorityLevelBootstrap;
import it.chalmers.gamma.app.bootstrap.ApiKeyBootstrap;
import it.chalmers.gamma.app.bootstrap.AuthorityLevelBootstrap;
import it.chalmers.gamma.app.bootstrap.ClientBootstrap;
import it.chalmers.gamma.app.bootstrap.EnsureAnAdminUserBootstrap;
import it.chalmers.gamma.app.bootstrap.EnsureAppSettingsBootstrap;
import it.chalmers.gamma.app.bootstrap.GroupBootstrap;
import it.chalmers.gamma.app.bootstrap.MiscBootstrap;
import it.chalmers.gamma.app.bootstrap.PostBootstrap;
import it.chalmers.gamma.app.bootstrap.SuperGroupBootstrap;
import it.chalmers.gamma.app.bootstrap.UserBootstrap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BootstrapRunner {

    @Bean
    public CommandLineRunner runBootStrap(AdminAuthorityLevelBootstrap adminAuthorityLevelBootstrap,
                                          ApiKeyBootstrap apiKeyBootstrap,
                                          AuthorityLevelBootstrap authorityLevelBootstrap,
                                          ClientBootstrap clientBootstrap,
                                          EnsureAnAdminUserBootstrap ensureAnAdminUserBootstrap,
                                          EnsureAppSettingsBootstrap ensureAppSettingsBootstrap,
                                          GroupBootstrap groupBootstrap,
                                          MiscBootstrap miscBootstrap,
                                          PostBootstrap postBootstrap,
                                          SuperGroupBootstrap superGroupBootstrap,
                                          UserBootstrap userBootstrap) {
        return (args) -> {
            miscBootstrap.runImageBootstrap();

            ensureAppSettingsBootstrap.ensureAppSettings();

            adminAuthorityLevelBootstrap.ensureAdminAuthorityLevel();
            ensureAnAdminUserBootstrap.ensureAnAdminUser();

            userBootstrap.createUsers();
            postBootstrap.createPosts();
            superGroupBootstrap.createSuperGroups();
            groupBootstrap.createGroups();
            authorityLevelBootstrap.createAuthorities();

            clientBootstrap.runOauthClient();
            apiKeyBootstrap.ensureApiKeys();
        };
    }

}
