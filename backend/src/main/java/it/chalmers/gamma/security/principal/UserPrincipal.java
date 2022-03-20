package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// This UserPrincipal is only used when authentication with UsernamePasswordAuthenticationToken
public non-sealed interface UserPrincipal extends GammaPrincipal, UserDetails {
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
