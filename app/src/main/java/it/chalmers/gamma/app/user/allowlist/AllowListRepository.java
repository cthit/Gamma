package it.chalmers.gamma.app.user.allowlist;

import it.chalmers.gamma.app.user.domain.Cid;

import java.util.List;

public interface AllowListRepository {

    void allow(Cid cid) throws AlreadyAllowedException;

    void remove(Cid cid) throws NotOnAllowListException;

    boolean isAllowed(Cid cid);

    List<Cid> getAllowList();

    class AlreadyAllowedException extends Exception {
    }

    class NotOnAllowListException extends RuntimeException {
    }

}
