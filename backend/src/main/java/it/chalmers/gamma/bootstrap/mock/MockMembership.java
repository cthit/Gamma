package it.chalmers.gamma.bootstrap.mock;

import java.util.UUID;

public class MockMembership {

    private UUID userId;
    private UUID postId;
    private String unofficialPostName;

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
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
