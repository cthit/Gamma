package it.chalmers.gamma.app.user.whitelist;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.Cid;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;

@Service
public class WhitelistFacade extends Facade {

    private final WhitelistRepository whitelistRepository;

    public WhitelistFacade(AccessGuard accessGuard,
                           WhitelistRepository whitelistRepository) {
        super(accessGuard);
        this.whitelistRepository = whitelistRepository;
    }

    public List<String> getWhitelist() {
        this.accessGuard.require(isAdmin());

        return this.whitelistRepository.getWhitelist()
                .stream()
                .map(Cid::value)
                .toList();
    }

    public void whitelist(String cid) throws WhitelistRepository.AlreadyWhitelistedException {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.WHITELIST)
        );

        this.whitelistRepository.whitelist(new Cid(cid));
    }

    public void removeFromWhitelist(String cid) throws WhitelistRepository.NotWhitelistedException {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.WHITELIST)
        );

        this.whitelistRepository.remove(new Cid(cid));
    }

    public boolean isWhitelisted(String cid) {
        this.accessGuard.requireEither(
                isAdmin(),
                isApi(ApiKeyType.WHITELIST)
        );

        return this.whitelistRepository.isWhitelisted(new Cid(cid));
    }

}
