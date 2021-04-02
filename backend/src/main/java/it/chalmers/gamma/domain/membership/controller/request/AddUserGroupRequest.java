package it.chalmers.gamma.domain.membership.controller.request;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import javax.validation.constraints.NotEmpty;

public class AddUserGroupRequest {

    @NotEmpty(message = "USER_MUST_BE_PROVIDED")
    private UserId userId;

    @NotEmpty(message = "POST_MUST_BE_PROVIDED")
    private PostId postId;
    private String unofficialName;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public PostId getPostId() {
        return postId;
    }

    public void setPostId(PostId postId) {
        this.postId = postId;
    }

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

}
