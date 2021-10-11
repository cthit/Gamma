package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "membership")
public class MembershipEntity extends ImmutableEntity<MembershipPK> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    protected MembershipEntity() {}

    protected MembershipEntity(MembershipPK id, String unofficialPostName) {
        this.id = id;
        this.unofficialPostName = unofficialPostName;
    }

    @Override
    public MembershipPK domainId() {
        return this.id;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

}
