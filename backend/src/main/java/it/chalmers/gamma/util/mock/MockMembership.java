package it.chalmers.gamma.util.mock;

import java.util.UUID;

public class MockMembership {

    private UUID userId;
    private UUID postId;
    private String unofficialPostName;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }
}
