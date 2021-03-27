package it.chalmers.gamma.util.domain.abstraction;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

public interface GetEntity<S extends Id, T extends DTO> {

    T get(S id) throws EntityNotFoundException;

}
