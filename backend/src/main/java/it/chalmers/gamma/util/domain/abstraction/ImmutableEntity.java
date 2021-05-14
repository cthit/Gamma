package it.chalmers.gamma.util.domain.abstraction;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImmutableEntity<I extends Id<?>, D extends DTO> extends AbstractEntity<I, D> {

}