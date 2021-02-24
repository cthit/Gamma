package it.chalmers.gamma.domain;

import java.util.List;

public interface GetAllEntities<T extends DTO> {

    List<T> getAll();

}
