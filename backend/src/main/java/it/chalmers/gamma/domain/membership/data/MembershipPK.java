package it.chalmers.gamma.domain.membership.data;

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


    public MembershipPK(UUID userId, UUID groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public MembershipPK() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }

    @Override
    public String toString() {
        return "MembershipPK{" +
                "userId=" + userId +
                ", groupId=" + groupId +
                '}';
    }
}
