package it.chalmers.gamma.requests;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class AuthorizationRequest {
    @NotNull
    private String post;
    @NotNull
    private String superGroup;
    @NotNull
    private String authority;

    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authorization) {
        this.authority = authorization;
    }

    public String getPost() {
        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(String superGroup) {
        this.superGroup = superGroup;
    }

    @Override
    public String toString() {
        return "AuthorizationRequest{"
            + "post='" + this.post + '\''
            + ", superGroup='" + this.superGroup + '\''
            + ", authority'" + this.authority + '\''
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorizationRequest that = (AuthorizationRequest) o;
        return Objects.equals(this.post, that.post)
            && Objects.equals(this.superGroup, that.superGroup)
            && Objects.equals(this.authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.superGroup, this.authority);
    }
}
