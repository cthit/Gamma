package it.chalmers.gamma.app.facade.internal;

import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.facade.Facade;
import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.repository.WhitelistRepository;
import it.chalmers.gamma.app.domain.user.Cid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhitelistFacade extends Facade {

    private final WhitelistRepository whitelistRepository;

    public WhitelistFacade(AccessGuardUseCase accessGuard,
                           WhitelistRepository whitelistRepository) {
        super(accessGuard);
        this.whitelistRepository = whitelistRepository;
    }

    public List<String> getWhitelist() {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        return this.whitelistRepository.getWhitelist()
                .stream()
                .map(Cid::value)
                .toList();
    }

    public void whitelist(String cid) throws WhitelistRepository.AlreadyWhitelistedException {
        this.accessGuard.require()
                .isAdmin()
                .or()
                .isApi(ApiKeyType.WHITELIST)
                .ifNotThrow();

        this.whitelistRepository.whitelist(new Cid(cid));
    }

    public void removeFromWhitelist(String cid) throws WhitelistRepository.CidIsNotWhitelistedException {
        this.accessGuard.require()
                .isAdmin()
                .or()
                .isApi(ApiKeyType.WHITELIST)
                .ifNotThrow();

        this.whitelistRepository.remove(new Cid(cid));
    }

    public boolean isWhitelisted(String cid) {
        this.accessGuard.require()
                .isAdmin()
                .or()
                .isApi(ApiKeyType.WHITELIST)
                .ifNotThrow();

        return this.whitelistRepository.isWhitelisted(new Cid(cid));
    }

}
