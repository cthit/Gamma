package it.chalmers.gamma.util.domain.abstraction;

public interface BaseEntity<D extends DTO> {

    D toDTO();

}
