package it.chalmers.gamma.requests;

import java.util.Objects;

public class AuthorizationLevelRequest {
    String AuthorityLevel;

    public String getAuthorityLevel() {
        return AuthorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        AuthorityLevel = authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationLevelRequest that = (AuthorizationLevelRequest) o;
        return Objects.equals(AuthorityLevel, that.AuthorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AuthorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorizationLevelRequest{" +
                "AuthorityLevel='" + AuthorityLevel + '\'' +
                '}';
    }
}
