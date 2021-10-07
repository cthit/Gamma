package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.port.repository.WhitelistRepository;
import it.chalmers.gamma.app.domain.user.Cid;
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

    public List<String> getWhitelist() {
        accessGuard.requireIsAdminOrApi();
        return this.whitelistRepository.getWhitelist()
                .stream()
                .map(Cid::value)
                .toList();
    }

    public void whitelist(String cid) throws WhitelistRepository.AlreadyWhitelistedException {
        accessGuard.requireIsAdminOrApi();
        this.whitelistRepository.whitelist(new Cid(cid));
    }

    public void removeFromWhitelist(String cid) throws WhitelistRepository.CidIsNotWhitelistedException {
        accessGuard.requireIsAdminOrApi();
        this.whitelistRepository.remove(new Cid(cid));
    }

    public boolean isWhitelisted(String cid) {
        accessGuard.requireIsAdminOrApi();
        return this.whitelistRepository.isWhitelisted(new Cid(cid));
    }

}
