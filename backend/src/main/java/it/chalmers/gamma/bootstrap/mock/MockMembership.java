package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.user.UserId;

import java.util.UUID;

public class MockMembership {

    private UserId userId;
    private UUID postId;
    private String unofficialPostName;

    public UserId getUserId() {
        return this.userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UUID getPostId() {
        return this.postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }
}
