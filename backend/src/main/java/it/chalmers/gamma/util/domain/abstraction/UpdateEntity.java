package it.chalmers.gamma.util.domain.abstraction;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

public interface UpdateEntity<D extends DTO> {

    void update(D d) throws EntityNotFoundException;

}
