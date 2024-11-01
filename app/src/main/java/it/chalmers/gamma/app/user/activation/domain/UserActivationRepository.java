package it.chalmers.gamma.app.user.activation.domain;

import it.chalmers.gamma.app.user.domain.Cid;
import java.util.List;

public interface UserActivationRepository {

  UserActivationToken createActivationToken(Cid cid) throws CidNotAllowedException;

  List<UserActivation> getAll();

  boolean isTokenValid(UserActivationToken token);

  Cid useToken(UserActivationToken token);

  void removeActivation(Cid cid) throws CidNotActivatedException;

  int removeInvalidActivationCodes();

  class TokenNotActivatedRuntimeException extends RuntimeException {}

  class CidNotActivatedException extends RuntimeException {}

  class CidNotAllowedException extends Exception {}
}
