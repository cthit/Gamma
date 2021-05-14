package it.chalmers.gamma.internal.supergrouptype.service;

import it.chalmers.gamma.util.domain.abstraction.SingleImmutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "super_group_type")
public class SuperGroupType extends SingleImmutableEntity<SuperGroupTypeName> {

    @EmbeddedId
    private SuperGroupTypeName superGroupTypeName;

    protected SuperGroupType() { }

    protected SuperGroupType(SuperGroupTypeName superGroupTypeName) {
        this.superGroupTypeName = superGroupTypeName;
    }

    @Override
    protected SuperGroupTypeName get() {
        return this.superGroupTypeName;
    }
}
