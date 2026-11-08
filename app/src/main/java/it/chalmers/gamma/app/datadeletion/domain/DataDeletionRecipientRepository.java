package it.chalmers.gamma.app.datadeletion.domain;

import it.chalmers.gamma.app.common.Email;
import java.util.List;

public interface DataDeletionRecipientRepository {

  void add(Email email) throws RecipientAlreadyExistsException;

  void remove(Email email) throws RecipientNotFoundException;

  List<Email> getAll();

  class RecipientAlreadyExistsException extends Exception {}

  class RecipientNotFoundException extends Exception {}
}
