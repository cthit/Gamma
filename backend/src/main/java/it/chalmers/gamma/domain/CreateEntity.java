package it.chalmers.gamma.domain;

public interface CreateEntity<T extends DTO> {

    void create(T newEntity) throws EntityAlreadyExistsException;

}
