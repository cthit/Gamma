package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.app.whitelist.WhitelistRepository;
import it.chalmers.gamma.domain.user.Cid;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WhitelistRepositoryAdapter implements WhitelistRepository {

    @Override
    public void whitelist(Cid cid) throws AlreadyWhitelistedException {

    }

    @Override
    public void remove(Cid cid) throws CidIsNotWhitelistedException {

    }

    @Override
    public boolean isWhitelisted(Cid cid) {
        return false;
    }

    @Override
    public List<Cid> getWhitelist() {
        return Collections.emptyList();
    }
}
