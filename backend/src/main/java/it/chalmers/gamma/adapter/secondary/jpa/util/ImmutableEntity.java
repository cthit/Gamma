package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.domain.Id;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<I extends Id<?>, D extends DTO> extends AbstractEntity<I, D> {

}