package it.chalmers.gamma.util.domain.abstraction;

public abstract class BaseEntity<D extends DTO> {

    protected abstract D toDTO();

}
