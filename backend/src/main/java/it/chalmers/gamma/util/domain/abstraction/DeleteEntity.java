package it.chalmers.gamma.util.domain.abstraction;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityHasUsagesException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

public interface DeleteEntity<T extends Id> {

    void delete(T id) throws EntityNotFoundException, EntityHasUsagesException;

}
