package it.chalmers.gamma.approval.data;

import it.chalmers.gamma.approval.dto.UserApprovalDTO;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApproval {

    @EmbeddedId
    private UserApprovalPK id;

    public UserApprovalPK getId() {
        return this.id;
    }

    public void setId(UserApprovalPK id) {
        this.id = id;
    }

    public UserApprovalDTO toDTO() {
        return new UserApprovalDTO(this.id.getUser(), this.id.getClient());
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
