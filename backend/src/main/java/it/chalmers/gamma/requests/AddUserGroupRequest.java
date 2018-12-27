package it.chalmers.gamma.requests;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Year;
import java.util.Objects;

public class AddUserGroupRequest {
    @NotEmpty(message = "USER_MUST_BE_PROVIDED")
    private String user;
    @NotEmpty(message = "POST_MUST_BE_PROVIDED")
    private String post;
    private String unofficialName;

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPost() {
        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddUserGroupRequest that = (AddUserGroupRequest) o;
        return this.user.equals(that.user)
            && this.post.equals(that.post)
            && this.unofficialName.equals(that.unofficialName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user, this.post, this.unofficialName);
    }

    @Override
    public String toString() {
        return "AddUserGroupRequest{"
            + "user='" + this.user + '\''
            + ", post='" + this.post + '\''
            + ", unofficialName='" + this.unofficialName + '\''
            + '}';
    }
}
