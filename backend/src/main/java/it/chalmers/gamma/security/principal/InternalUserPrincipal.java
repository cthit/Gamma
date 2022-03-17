package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Only used when interacting with the Gamma Client.
 * Gives more access such as editing your own profile and doing other stuff.
 * Difference between this and ExternalUserAuthenticated is that ExternalUserAuthenticated
 * is created when another website has been authorized by the user to access the users' information.
 *
 * Separating these two scenarios helps the domain to guarantee that an external client cannot act as the user and edit
 * their information for example.
 */
public non-sealed interface InternalUserPrincipal extends GammaPrincipal, UserDetails {
    User get();
    boolean isAdmin();

    @Override
    default String getUsername() {
        return get().id().value().toString();
    }

    @Override
    default String getPassword() {
        return null;
    }

    @Override
    default boolean isAccountNonExpired() {
        return true;
    }

    @Override
    //TODO: Investigate more what this means.
    default boolean isAccountNonLocked() {
        return get().extended().locked();
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    default boolean isEnabled() {
        return true;
    }

    @Override
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
