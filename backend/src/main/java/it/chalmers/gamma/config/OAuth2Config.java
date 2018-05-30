package it.chalmers.gamma.config;

import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Qualifier("userDetailsService")
    private ITUserService itUserService;

    private AuthenticationManager authenticationManager;

    @Value("${it.oauth.tokenTimeout:3600}")
    private int expiration;

    public OAuth2Config(AuthenticationManager authenticationManager, ITUserService itUserService){
        this.authenticationManager = authenticationManager;
        this.itUserService = itUserService;
    }

    /*
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new PasswordEncoder(){

            @Override
            public String encode(CharSequence rawPassword) {
                System.out.println("To Encode: " + rawPassword);
                return null;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                System.out.println("Raw: " + rawPassword + "; encodedPassword: " + encodedPassword);
                return true;
            }
        };
    }*/

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) {
        configurer.authenticationManager(authenticationManager);
        configurer.userDetailsService(itUserService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("it")
                .secret("secret")
                .autoApprove(false)
                .accessTokenValiditySeconds(expiration)
                .scopes("read", "write")
                .authorizedGrantTypes("authorization_code")
                .resourceIds("resource");
    }

}
