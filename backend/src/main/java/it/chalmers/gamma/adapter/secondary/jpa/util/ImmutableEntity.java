package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.common.Id;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<I extends Id<?>> extends AbstractEntity<I> {

}