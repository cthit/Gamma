package it.chalmers.gamma.noaccountmembership;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.group.Group;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class NoAccountMembershipPK implements Serializable {

    @Column(name = "user_name")
    private String user;

    @JoinColumn(name = "fkit_group_id")
    private UUID groupId;

    public NoAccountMembershipPK(String user, UUID groupId) {
        this.user = user;
        this.groupId = groupId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
        NoAccountMembershipPK that = (NoAccountMembershipPK) o;
        return Objects.equals(user, that.user) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, groupId);
    }

    @Override
    public String toString() {
        return "NoAccountMembershipPK{" +
                "user='" + user + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
