package it.chalmers.gamma.membership;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class MembershipPK implements Serializable {

    @JoinColumn(name = "ituser_id")
    private UUID userId;

    @JoinColumn(name = "fkit_group_id")
    private UUID groupId;

    @JoinColumn(name = "post")
    private UUID postId;

    public MembershipPK(UUID userId, UUID groupId, UUID postId) {
        this.userId = userId;
        this.groupId = groupId;
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(groupId, that.groupId) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId, postId);
    }

    @Override
    public String toString() {
        return "MembershipPK{" +
                "userId=" + userId +
                ", groupId=" + groupId +
                ", postId=" + postId +
                '}';
    }
}
