package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "membership")
public class Membership extends MutableEntity<MembershipShallowDTO> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    protected Membership() {}

    protected Membership(MembershipShallowDTO membership) {
        assert(membership.postId() != null);
        assert(membership.groupId() != null);
        assert(membership.userId() != null);

        this.id = new MembershipPK(
                membership.postId(),
                membership.groupId(),
                membership.userId()
        );

        apply(membership);
    }

    @Override
    protected void apply(MembershipShallowDTO m) {
        assert(this.id.equals(new MembershipPK(m.postId(), m.groupId(), m.userId())));

        this.unofficialPostName = m.unofficialPostName();
    }

    @Override
    protected MembershipShallowDTO toDTO() {
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
