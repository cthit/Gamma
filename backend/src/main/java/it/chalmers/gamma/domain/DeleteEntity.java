package it.chalmers.gamma.domain;

public interface DeleteEntity<T extends Id> {

    void delete(T id) throws EntityNotFoundException, EntityHasUsagesException;

}
