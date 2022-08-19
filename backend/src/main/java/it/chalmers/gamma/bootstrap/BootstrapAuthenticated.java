package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.security.principal.LocalRunnerAuthenticationDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.time.Instant;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

public class BootstrapAuthenticated  extends AbstractAuthenticationToken {

    private final LocalRunnerAuthenticationDetails localRunnerPrincipal;

    public BootstrapAuthenticated() {
        super(NO_AUTHORITIES);
        setAuthenticated(true);

        final String instantiatedAt = "Local runner instantiated at" + Instant.now();
        localRunnerPrincipal = new LocalRunnerAuthenticationDetails() {
            @Override
            public String toString() {
                return instantiatedAt;
            }
        };
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return localRunnerPrincipal;
    }
}
