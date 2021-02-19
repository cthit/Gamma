package it.chalmers.gamma.domain.approval.data;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApproval {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApproval() {}

    public UserApproval(UserApprovalPK id) {
        this.id = id;
    }

    public UserApprovalPK getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserApproval that = (UserApproval) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ITUserApproval{"
            + "id=" + this.id
            + '}';
    }
}

