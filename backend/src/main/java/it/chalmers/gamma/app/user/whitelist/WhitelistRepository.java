package it.chalmers.gamma.app.user.whitelist;

import it.chalmers.gamma.app.user.domain.Cid;

import java.util.List;

public interface WhitelistRepository {

    void whitelist(Cid cid) throws AlreadyWhitelistedException;
    void remove(Cid cid) throws CidIsNotWhitelistedException;

    boolean isWhitelisted(Cid cid);

    List<Cid> getWhitelist();

    class AlreadyWhitelistedException extends Exception { }
    class CidIsNotWhitelistedException extends Exception { }

}