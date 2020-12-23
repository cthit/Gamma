package it.chalmers.gamma.user.request.requests;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;

public class AddUserGroupRequest {

    @NotEmpty(message = "USER_MUST_BE_PROVIDED")
    private String userId;

    @NotEmpty(message = "POST_MUST_BE_PROVIDED")
    private String post;
    private String unofficialName;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return this.userId.equals(that.userId)
            && this.post.equals(that.post)
            && this.unofficialName.equals(that.unofficialName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.post, this.unofficialName);
    }

    @Override
    public String toString() {
        return "AddUserGroupRequest{"
            + "userId='" + this.userId + '\''
            + ", post='" + this.post + '\''
            + ", unofficialName='" + this.unofficialName + '\''
            + '}';
    }
}
