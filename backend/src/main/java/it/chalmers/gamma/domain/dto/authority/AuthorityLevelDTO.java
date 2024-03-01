package it.chalmers.gamma.domain.dto.authority;

import java.util.Objects;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public class AuthorityLevelDTO implements GrantedAuthority {
    private final UUID id;
    private final String authorityLevel;

    public AuthorityLevelDTO(UUID id, String authorityLevel) {
        this.id = id;
        this.authorityLevel = authorityLevel;
    }

    public UUID getId() {
        return this.id;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorityLevelDTO that = (AuthorityLevelDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.authorityLevel, that.authorityLevel);

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorityLevelDTO{"
                + "id=" + id
                + ", authorityLevel='" + authorityLevel + '\''
                + '}';
    }
}
