package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.util.AbstractEntity;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "super_group_type")
public class SuperGroupTypeEntity extends AbstractEntity<String> {

    @Id
    @Column(name = "super_group_type_name")
    private String name;

    protected SuperGroupTypeEntity() {
    }

    public SuperGroupTypeEntity(SuperGroupType superGroupType) {
        this.name = superGroupType.getValue();
    }

    @Override
    public String getId() {
        return this.name;
    }
}
