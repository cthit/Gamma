package it.chalmers.gamma.internal.membership.service;


import it.chalmers.gamma.util.entity.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "membership")
public class MembershipEntity extends MutableEntity<MembershipPK, MembershipShallowDTO> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    protected MembershipEntity() {}

    protected MembershipEntity(MembershipShallowDTO membership) {
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
    public void apply(MembershipShallowDTO m) {
        assert(this.id.equals(new MembershipPK(m.postId(), m.groupId(), m.userId())));

        this.unofficialPostName = m.unofficialPostName();
    }

    @Override
    protected MembershipShallowDTO toDTO() {
        return new MembershipShallowDTO(
                this.id.get().postId(),
                this.id.get().groupId(),
                this.unofficialPostName,
                this.id.get().userId()
        );
    }

    @Override
    protected MembershipPK id() {
        return this.id;
    }

}
