package it.chalmers.gamma.domain;

/**
 *
 * @param <D> DTO of the Entity.
 */
public interface GEntity<D> {

    void apply(D d) throws IDsNotMatchingException;

}
