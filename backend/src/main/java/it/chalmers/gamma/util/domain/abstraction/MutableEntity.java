package it.chalmers.gamma.util.domain.abstraction;

public interface MutableEntity<D extends DTO> extends BaseEntity<D> {

    void apply(D d);

}
