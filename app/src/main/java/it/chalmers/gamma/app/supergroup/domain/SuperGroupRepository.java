package it.chalmers.gamma.app.supergroup.domain;

import java.util.List;
import java.util.Optional;

public interface SuperGroupRepository {

  void save(SuperGroup superGroup)
      throws NameAlreadyExistsRuntimeException, TypeNotFoundRuntimeException;

  void delete(SuperGroupId superGroupId)
      throws SuperGroupNotFoundException, SuperGroupIsUsedException;

  List<SuperGroup> getAll();

  List<SuperGroup> getAllByType(SuperGroupType superGroupType);

  Optional<SuperGroup> get(SuperGroupId superGroupId);

  class SuperGroupAlreadyExistsException extends Exception {}

  class SuperGroupNotFoundException extends Exception {}

  class SuperGroupIsUsedException extends Exception {}

  class NameAlreadyExistsRuntimeException extends RuntimeException {}

  class TypeNotFoundRuntimeException extends RuntimeException {}
}
