package it.chalmers.gamma.app.whitelist;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.user.Cid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhitelistFacade extends Facade {

    private final WhitelistRepository whitelistRepository;

    public WhitelistFacade(AccessGuard accessGuard,
                           WhitelistRepository whitelistRepository) {
        super(accessGuard);
        this.whitelistRepository = whitelistRepository;
    }

    public List<Cid> getWhitelist() {
        accessGuard.requireIsAdminOrApi();
        return this.whitelistRepository.getWhitelist();
    }

    public void whitelist(Cid cid) throws WhitelistRepository.AlreadyWhitelistedException {
        accessGuard.requireIsAdminOrApi();
        this.whitelistRepository.whitelist(cid);
    }

    public void removeFromWhitelist(Cid cid) throws WhitelistRepository.CidIsNotWhitelistedException {
        accessGuard.requireIsAdminOrApi();
        this.whitelistRepository.remove(cid);
    }

    public boolean isWhitelisted(Cid cid) {
        accessGuard.requireIsAdminOrApi();
        return this.whitelistRepository.isWhitelisted(cid);
    }

}
