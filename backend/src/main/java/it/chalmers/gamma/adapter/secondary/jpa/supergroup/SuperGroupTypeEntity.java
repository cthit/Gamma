package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "super_group_type")
public class SuperGroupTypeEntity extends SingleImmutableEntity<String> {

    @Id
    @Column(name = "super_group_type_name")
    private String name;

    protected SuperGroupTypeEntity() { }

    public SuperGroupTypeEntity(SuperGroupType superGroupType) {
        this.name = superGroupType.getValue();
    }

    @Override
    public String get() {
        return this.name;
    }
}
