package it.chalmers.gamma.domain.membership.data.db;

import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class MembershipPK implements Serializable, Id {

    @Embedded
    private PostId postId;

    @Embedded
    private GroupId groupId;

    @Embedded
    private UserId userId;

    protected MembershipPK() {

    }

    public MembershipPK(PostId postId, GroupId groupId, UserId userId) {
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

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }

    public PostId getPostId() {
        return postId;
    }

    public void setPostId(PostId postId) {
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
