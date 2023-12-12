package it.chalmers.demo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class GammaAuthoritiesMapper implements GrantedAuthoritiesMapper {

    /**
     * Spring always adds a default ROLE_USER role.
     * I use this to get the information to create proper authorities
     * The information is from the claims from when calling the userinfo endpoint from gamma
     * The of the "authorities" claim is
     * [
     *      {
     *          type: "GROUP",
     *          authority: "digit2018"
     *      },
     *      {
     *          type: "SUPERGROUP",
     *          authority: "digit"
     *      },
     *      {
     *          type: "AUTHORITY",
     *          authority: "admin"
     *      }
     * ]
     * The authorities that will be created is:
     * [
     *      "GROUP_DIGIT2018",
     *      "SUPERGROUP_DIGIT",
     *      "AUTHORITY_ADMIN"
     * ]
     */
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        Set<GrantedAuthority> newGrantedAuthorities = new HashSet<>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority instanceof OidcUserAuthority oidcUserAuthority) {
                OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
//                List<Map<String, String>> authorities = userInfo.getClaim("authorities");
//                for (Map<String, String> authority : authorities) {
//                    newGrantedAuthorities.add(
//                            new SimpleGrantedAuthority(
//                                    authority.get("type").toUpperCase()
//                                    + "_"
//                                    + authority.get("authority").toUpperCase()
//                            )
//                    );
//                }
            }
        }

        System.out.println(newGrantedAuthorities);

        return newGrantedAuthorities;
    }

}
