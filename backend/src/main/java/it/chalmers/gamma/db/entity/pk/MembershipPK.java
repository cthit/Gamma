package it.chalmers.gamma.db.entity.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MembershipPK implements Serializable {

    @Column(name = "ituser_id")
    private UUID ituserId;

    @Column(name = "fkit_group_id")
    private UUID fkitGroupId;

    public UUID getITUserId() {
        return ituserId;
    }

    public void setITUserId(UUID ituserId) {
        this.ituserId = ituserId;
    }

    public UUID getFKITGroupId() {
        return fkitGroupId;
    }

    public void setFKITGroupId(UUID fkitGroupId) {
        this.fkitGroupId = fkitGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(ituserId, that.ituserId) &&
                Objects.equals(fkitGroupId, that.fkitGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ituserId, fkitGroupId);
    }

    @Override
    public String toString() {
        return "MembershipPK{" +
                "ituserId=" + ituserId +
                ", fkitGroupId=" + fkitGroupId +
                '}';
    }
}
