package it.chalmers.gamma.app.supergrouptype.service;

import it.chalmers.gamma.app.domain.SuperGroupType;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

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
