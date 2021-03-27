package it.chalmers.gamma.domain.membership.data.db;

import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.membership.data.dto.MembershipShallowDTO;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "membership")
public class Membership implements MutableEntity<MembershipShallowDTO> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    protected Membership() {}

    public Membership(MembershipShallowDTO membership) {
        assert(membership.getPostId() != null);
        assert(membership.getGroupId() != null);
        assert(membership.getUserId() != null);

        this.id = new MembershipPK(
                membership.getPostId(),
                membership.getGroupId(),
                membership.getUserId()
        );

        apply(membership);
    }

    @Override
    public void apply(MembershipShallowDTO m) {
        assert(this.id.equals(new MembershipPK(m.getPostId(), m.getGroupId(), m.getUserId())));

        this.unofficialPostName = m.getUnofficialPostName();
    }

    @Override
    public MembershipShallowDTO toDTO() {
        return new MembershipShallowDTO(
                this.id.getPostId(),
                this.id.getGroupId(),
                this.unofficialPostName,
                this.id.getUserId()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "Membership{"
            + "id=" + this.id
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + '}';
    }
}
