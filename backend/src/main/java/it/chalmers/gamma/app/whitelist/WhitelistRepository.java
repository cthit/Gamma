package it.chalmers.gamma.app.whitelist;

import it.chalmers.gamma.app.domain.Cid;

public interface WhitelistRepository {

    void whitelist(Cid cid) throws AlreadyWhitelistedException;
    void remove(Cid cid) throws CidIsNotWhitelistedException;

    boolean isWhitelisted(Cid cid);

    class AlreadyWhitelistedException extends Exception { }
    class CidIsNotWhitelistedException extends Exception { }

}
