package it.chalmers.gamma.app.supergroup.domain;

import java.util.List;

public interface SuperGroupTypeRepository {

    void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException;
    void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException;

    List<SuperGroupType> getAll();

    class SuperGroupTypeAlreadyExistsException extends Exception {
        public SuperGroupTypeAlreadyExistsException(String value) {
            super("Super group type: " + value + " already exists");
        }
    }
    class SuperGroupTypeNotFoundException extends Exception { }
    class SuperGroupTypeHasUsagesException extends Exception { }

}
