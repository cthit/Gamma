package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;

import java.util.List;
import java.util.Optional;

public interface SuperGroupRepository {

    void create(SuperGroup superGroup);
    void save(SuperGroup superGroup);
    void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException;

    List<SuperGroup> getAll();
    Optional<SuperGroup> get(SuperGroupId superGroupId);

    class SuperGroupNotFoundException extends Exception { }


}
