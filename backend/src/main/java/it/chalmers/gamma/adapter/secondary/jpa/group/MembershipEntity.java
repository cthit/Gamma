package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_membership")
public class MembershipEntity extends ImmutableEntity<MembershipPK> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    protected MembershipEntity() {
    }

    protected MembershipEntity(MembershipPK id, String unofficialPostName) {
        this.id = id;
        this.unofficialPostName = unofficialPostName;
    }

    @Override
    public MembershipPK getId() {
        return this.id;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

}
