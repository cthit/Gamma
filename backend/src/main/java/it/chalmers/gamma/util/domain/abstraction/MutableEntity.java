package it.chalmers.gamma.util.domain.abstraction;

public abstract class MutableEntity<D extends DTO> extends BaseEntity<D> {

    protected abstract void apply(D d);

}
