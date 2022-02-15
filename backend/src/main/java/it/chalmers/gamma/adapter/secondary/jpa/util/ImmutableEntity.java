package it.chalmers.gamma.adapter.secondary.jpa.util;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<ID> extends AbstractEntity<ID> {

}