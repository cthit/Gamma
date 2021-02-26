package it.chalmers.gamma.domain.membership.data.dto;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import java.util.Objects;

public class MembershipShallowDTO implements DTO {

    private final PostId postId;
    private final GroupId groupId;
    private final UserId userId;
    private final String unofficialPostName;

    public MembershipShallowDTO(PostId postId,
                                GroupId groupId,
                                String unofficialPostName,
                                UserId userId) {
        this.postId = postId;
        this.groupId = groupId;
        this.unofficialPostName = unofficialPostName;
        this.userId = userId;
    }

    public PostId getPostId() {
        return postId;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipShallowDTO that = (MembershipShallowDTO) o;
        return Objects.equals(postId, that.postId) && Objects.equals(groupId, that.groupId) && Objects.equals(unofficialPostName, that.unofficialPostName) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, groupId, unofficialPostName, userId);
    }

    @Override
    public String toString() {
        return "MembershipShallowDTO{" +
                "postId=" + postId +
                ", groupId=" + groupId +
                ", unofficialPostName='" + unofficialPostName + '\'' +
                ", userId=" + userId +
                '}';
    }
}