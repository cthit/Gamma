package it.chalmers.gamma.domain;

public interface GetEntity<S extends Id, T extends DTO> {

    T get(S id) throws EntityNotFoundException;

}
