package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.common.Id;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<ID> extends AbstractEntity<ID> {

}