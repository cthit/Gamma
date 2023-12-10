package it.chalmers.gamma.app.user.allowlist;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.Cid;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;

@Service
public class AllowListFacade extends Facade {

    private final AllowListRepository allowListRepository;

    public AllowListFacade(AccessGuard accessGuard,
                           AllowListRepository allowListRepository) {
        super(accessGuard);
        this.allowListRepository = allowListRepository;
    }

    public List<String> getAllowList() {
        this.accessGuard.require(isAdmin());

        return this.allowListRepository.getAllowList()
                .stream()
                .map(Cid::value)
                .toList();
    }

    public void allow(String cid) throws AllowListRepository.AlreadyAllowedException {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.ALLOW_LIST)
        );

        this.allowListRepository.allow(new Cid(cid));
    }

    public void removeFromAllowList(String cid) throws AllowListRepository.NotOnAllowListException {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.ALLOW_LIST)
        );

        this.allowListRepository.remove(new Cid(cid));
    }

    public boolean isAllowed(String cid) {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.ALLOW_LIST)
        );

        return this.allowListRepository.isAllowed(new Cid(cid));
    }

}
