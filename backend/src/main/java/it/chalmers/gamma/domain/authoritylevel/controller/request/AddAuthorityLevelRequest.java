package it.chalmers.gamma.domain.authoritylevel.controller.request;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;

public class AddAuthorityLevelRequest {

    @NotEmpty(message = "AUTHORITY_LEVEL_MUST_BE_PROVIDED")
    private AuthorityLevelName authorityLevel;

    public AuthorityLevelName getAuthorityLevel() {
        return this.authorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        this.authorityLevel = new AuthorityLevelName(authorityLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddAuthorityLevelRequest that = (AddAuthorityLevelRequest) o;
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
