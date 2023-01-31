package it.chalmers.gamma.adapter.secondary.jpa.util;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<ID> extends AbstractEntity<ID> {

}