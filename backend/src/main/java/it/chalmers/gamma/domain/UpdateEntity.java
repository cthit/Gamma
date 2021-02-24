package it.chalmers.gamma.domain;

public interface UpdateEntity<D extends DTO> {

    void update(D d) throws EntityNotFoundException;

}
