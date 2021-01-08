package it.chalmers.gamma.db.entity.pk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.group.Group;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class NoAccountMembershipPK implements Serializable {

    private static final long serialVersionUID = 6624119509779427L;
    @Column(name = "user_name")
    private String itUser;
    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private Group group;

    public String getITUser() {
        return this.itUser;
    }

    public void setITUser(String ituserId) {
        this.itUser = ituserId;
    }

    public Group getFKITGroup() {
        return this.group;
    }

    public void setFKITGroup(Group groupId) {
        this.group = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoAccountMembershipPK that = (NoAccountMembershipPK) o;
        return Objects.equals(this.itUser, that.itUser)
                && Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.itUser, this.group);
    }

    @Override
    public String toString() {
        return "NoAccountMembershipPK{"
                + "itUser='" + this.itUser + '\''
                + ", group=" + this.group
                + '}';
    }
}
