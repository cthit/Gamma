package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "super_group_type")
public class SuperGroupTypeEntity extends SingleImmutableEntity<SuperGroupType> {

    @Id
    @Column(name = "super_group_type_name")
    private String superGroupType;

    protected SuperGroupTypeEntity() { }

    protected SuperGroupTypeEntity(SuperGroupType superGroupType) {
        this.superGroupType = superGroupType.getValue();
    }

    @Override
    protected SuperGroupType get() {
        return SuperGroupType.valueOf(this.superGroupType);
    }
}
