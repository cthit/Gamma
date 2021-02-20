package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import java.util.UUID;

public class MockMembership {

    private UserId userId;
    private PostId postId;
    private String unofficialPostName;

    public UserId getUserId() {
        return this.userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public PostId getPostId() {
        return this.postId;
    }

    public void setPostId(PostId postId) {
        this.postId = postId;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }
}
