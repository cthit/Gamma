package it.chalmers.gamma.security.oauth2;

import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    private final UserRepository userRepository;
    private final String baseUrl;
    private final String contextPath;

    public UserInfoMapper(UserRepository userRepository,
                          @Value("${application.base-uri}") String baseUrl,
                          @Value("${server.servlet.context-path}") String contextPath) {
        this.userRepository = userRepository;
        this.baseUrl = baseUrl;
        this.contextPath = contextPath;
    }


    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
        JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
        User me = this.userRepository.get(UserId.valueOf(principal.getName()))
                .orElseThrow();

        /*
         * Available scopes are profile, email.
         * The prefix that spring-authorization-server adds in SCOPE_
         */
        final String PROFILE_SCOPE = "SCOPE_profile";
        final String EMAIL_SCOPE = "SCOPE_email";

        Map<String, Object> claims = new HashMap<>(principal.getToken().getClaims());
        Collection<GrantedAuthority> scopes = principal.getAuthorities();

        for (GrantedAuthority scope : scopes) {
            if (scope.getAuthority().equals(PROFILE_SCOPE)) {
                //https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
                claims.put("name", me.firstName() + " '" + me.nick() + "' " + me.lastName());
                claims.put("given_name", me.firstName());
                claims.put("family_name", me.lastName());
                claims.put("nickname", me.nick());
                claims.put("locale", me.language().toString().toLowerCase());

                //TODO: Should the avatar uri be a final variable
                claims.put("picture", this.baseUrl
                        + this.contextPath
                        + "/images/user/avatar/"
                        + me.id().toString()
                );

                // Non-standard claims.
                claims.put("cid", me.cid());


                // To make sure that authorities are up-to-date, maybe this should be a separate thing
//                claims.put("authorities", me.authorities());
            } else if (scope.getAuthority().equals(EMAIL_SCOPE)) {
                claims.put("email", me.extended().email().value());
            }
        }

        return new OidcUserInfo(claims);
    }
}
