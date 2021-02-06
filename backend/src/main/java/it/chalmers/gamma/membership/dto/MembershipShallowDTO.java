package it.chalmers.gamma.membership.dto;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.dto.UserDTO;

import java.util.Objects;
import java.util.UUID;

public class MembershipShallowDTO {

    private final UUID postId;
    private final UUID groupId;
    private final String unofficialPostName;
    private final UUID userId;

    public MembershipShallowDTO(UUID postId,
                                UUID groupId,
                                String unofficialPostName,
                                UUID userId) {
        this.postId = postId;
        this.groupId = groupId;
        this.unofficialPostName = unofficialPostName;
        this.userId = userId;
    }

    public UUID getPostId() {
        return postId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

    public UUID getUserId() {
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