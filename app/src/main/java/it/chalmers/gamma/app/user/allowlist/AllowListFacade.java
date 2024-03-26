package it.chalmers.gamma.app.user.allowlist;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AllowListFacade extends Facade {

  private final AllowListRepository allowListRepository;
  private final UserRepository userRepository;

  public AllowListFacade(
      AccessGuard accessGuard,
      AllowListRepository allowListRepository,
      UserRepository userRepository) {
    super(accessGuard);
    this.allowListRepository = allowListRepository;
    this.userRepository = userRepository;
  }

  public List<String> getAllowList() {
    this.accessGuard.require(isAdmin());

    return this.allowListRepository.getAllowList().stream().map(Cid::value).toList();
  }

  public void allow(String cidRaw)
      throws AllowListRepository.AlreadyAllowedException, AlreadyAUserException {
    Cid cid = new Cid(cidRaw);

    this.accessGuard.requireEither(isAdmin(), isApi(ApiKeyType.ALLOW_LIST));

    if (this.userRepository.get(cid).isPresent()) {
      throw new AlreadyAUserException();
    }

    this.allowListRepository.allow(cid);
  }

  public void removeFromAllowList(String cid) throws AllowListRepository.NotOnAllowListException {
    this.accessGuard.requireEither(isAdmin(), isApi(ApiKeyType.ALLOW_LIST));

    this.allowListRepository.remove(new Cid(cid));
  }

  public boolean isAllowed(String cid) {
    this.accessGuard.requireEither(isAdmin(), isApi(ApiKeyType.ALLOW_LIST));

    return this.allowListRepository.isAllowed(new Cid(cid));
  }

  public static final class AlreadyAUserException extends Exception {
    public AlreadyAUserException() {
      super("Cid is already a user");
    }
  }
}
