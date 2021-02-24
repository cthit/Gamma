package it.chalmers.gamma.domain;

public interface BaseEntity<D extends DTO> {

    D toDTO();

}
