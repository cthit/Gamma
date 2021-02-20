package it.chalmers.gamma.domain.noaccountmembership;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.domain.group.GroupId;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class NoAccountMembershipPK implements Serializable {

    @Column(name = "user_name")
    private String user;

    @Embedded
    private GroupId groupId;

    protected NoAccountMembershipPK() {}

    public NoAccountMembershipPK(String user, GroupId groupId) {
        this.user = user;
        this.groupId = groupId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "NoAccountMembershipPK{" +
                "user='" + user + '\'' +
                ", groupId=" + groupId +
                '}';
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
}
