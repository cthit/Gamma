package it.chalmers.gamma.domain;

public interface MutableEntity<D extends DTO> extends BaseEntity<D> {

    void apply(D d);

}
