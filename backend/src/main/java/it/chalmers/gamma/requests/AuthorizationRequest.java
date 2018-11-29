package it.chalmers.gamma.requests;

import java.util.Objects;

public class AuthorizationRequest {
    private String post;
    private String group;
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

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "AuthorizationRequest{"
            + "post='" + this.post + '\''
            + ", group='" + this.group + '\''
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
            && Objects.equals(this.group, that.group)
            && Objects.equals(this.authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.group, this.authority);
    }
}
