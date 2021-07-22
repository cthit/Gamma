package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroupType;

import java.util.List;
import java.util.Optional;

public interface SuperGroupRepository {

    void create(SuperGroup superGroup) throws SuperGroupAlreadyExistsException;
    void save(SuperGroup superGroup) throws SuperGroupNotFoundException;
    void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException;

    List<SuperGroup> getAll();
    List<SuperGroup> getAllByType(SuperGroupType superGroupType);

    Optional<SuperGroup> get(SuperGroupId superGroupId);

    class SuperGroupAlreadyExistsException extends Exception { }
    class SuperGroupNotFoundException extends Exception { }


}
