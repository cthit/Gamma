package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.domain.supergroup.SuperGroupType;

import java.util.List;

public interface SuperGroupTypeRepository {

    void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException;
    void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException;

    List<SuperGroupType> getAll();

    class SuperGroupTypeAlreadyExistsException extends Exception { }
    class SuperGroupTypeNotFoundException extends Exception { }
    class SuperGroupTypeHasUsagesException extends Exception { }

}
