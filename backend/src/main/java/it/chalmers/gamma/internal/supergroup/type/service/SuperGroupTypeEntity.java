package it.chalmers.gamma.internal.supergroup.type.service;

import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.util.domain.abstraction.SingleImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "super_group_type")
public class SuperGroupTypeEntity extends SingleImmutableEntity<SuperGroupType> {

    @EmbeddedId
    private SuperGroupType superGroupType;

    protected SuperGroupTypeEntity() { }

    protected SuperGroupTypeEntity(SuperGroupType superGroupType) {
        this.superGroupType = superGroupType;
    }

    @Override
    protected SuperGroupType get() {
        return this.superGroupType;
    }
}
