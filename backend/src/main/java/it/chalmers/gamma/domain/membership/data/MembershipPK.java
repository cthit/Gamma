package it.chalmers.gamma.domain.membership.data;

import it.chalmers.gamma.domain.user.UserId;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class MembershipPK implements Serializable {

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "fkit_group_id")
    private UUID groupId;

    @Embedded
    private UserId userId;

    protected MembershipPK() {

    }

    public MembershipPK(UUID postId, UUID groupId, UserId userId) {
        this.postId = postId;
        this.groupId = groupId;
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
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
        return Objects.equals(postId, that.postId) && Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, groupId, userId);
    }
}
