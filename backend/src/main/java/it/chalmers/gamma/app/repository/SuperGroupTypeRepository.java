package it.chalmers.gamma.app.repository;

import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;

import java.util.List;

public interface SuperGroupTypeRepository {

    void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException;
    void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException;

    List<SuperGroupType> getAll();

    class SuperGroupTypeAlreadyExistsException extends Exception { }
    class SuperGroupTypeNotFoundException extends Exception { }
    class SuperGroupTypeHasUsagesException extends Exception { }

}
