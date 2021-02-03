package it.chalmers.gamma.requests;

import java.util.Objects;
import java.util.UUID;

public class EditMembershipRequest {

    private UUID postId;
    private String unofficialName;

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

    @Override
    public String toString() {
        return "EditMembershipRequest{"
            + "unofficialName='" + this.unofficialName + '\''
            + "post='" + this.postId + '\''
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
        EditMembershipRequest that = (EditMembershipRequest) o;
        return Objects.equals(this.unofficialName, that.unofficialName) && Objects.equals(this.postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.unofficialName, this.postId);
    }
}
