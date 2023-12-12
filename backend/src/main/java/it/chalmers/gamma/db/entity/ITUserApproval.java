package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.ITUserApprovalPK;
import it.chalmers.gamma.domain.dto.user.ITUserApprovalDTO;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class ITUserApproval {

    @EmbeddedId
    private ITUserApprovalPK id;

    public ITUserApprovalPK getId() {
        return this.id;
    }

    public void setId(ITUserApprovalPK id) {
        this.id = id;
    }

    public ITUserApprovalDTO toDTO() {
        return new ITUserApprovalDTO(this.id.getItUser().toDTO(), this.id.getItClient().toDTO());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITUserApproval that = (ITUserApproval) o;
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

