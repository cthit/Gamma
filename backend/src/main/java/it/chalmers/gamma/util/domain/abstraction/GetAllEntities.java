package it.chalmers.gamma.util.domain.abstraction;

import java.util.List;

public interface GetAllEntities<T extends DTO> {

    List<T> getAll();

}
