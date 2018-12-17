package it.chalmers.gamma.requests;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

public class AuthorizationLevelRequest {
    @NotEmpty(message = "an authorityLevel must be supplied")
    private String authorityLevel;

    public String getAuthorityLevel() {
        return this.authorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorizationLevelRequest that = (AuthorizationLevelRequest) o;
        return Objects.equals(this.authorityLevel, that.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorizationLevelRequest{"
            + "AuthorityLevel='" + this.authorityLevel + '\''
            + '}';
    }
}
