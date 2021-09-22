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
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Cid cid) throws CidIsNotWhitelistedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWhitelisted(Cid cid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Cid> getWhitelist() {
        throw new UnsupportedOperationException();
    }
}
