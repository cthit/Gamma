package it.chalmers.gamma.requests;

import java.util.Objects;

public class AuthorizationRequest {
    private String Post;
    private String group;
    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authorization) {
        this.authority = authorization;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "AuthorizationRequest{" +
                "Post='" + Post + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationRequest that = (AuthorizationRequest) o;
        return Objects.equals(Post, that.Post) &&
                Objects.equals(group, that.group) &&
                Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {

        return Objects.hash(Post, group, authority);
    }
}
