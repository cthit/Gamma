package it.chalmers.gamma.util.domain.abstraction;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;

public interface CreateEntity<T extends DTO> {

    void create(T newEntity) throws EntityAlreadyExistsException;

}
