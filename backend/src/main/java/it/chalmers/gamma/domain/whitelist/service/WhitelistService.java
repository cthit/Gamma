package it.chalmers.gamma.domain.whitelist.service;

import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.whitelist.data.Whitelist;
import it.chalmers.gamma.domain.whitelist.data.WhitelistRepository;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService implements CreateEntity<Cid>, DeleteEntity<Cid> {

    private final UserFinder userFinder;
    private final WhitelistFinder whitelistFinder;
    private final WhitelistRepository whitelistRepository;

    public WhitelistService(UserFinder userFinder,
                            WhitelistFinder whitelistFinder,
                            WhitelistRepository whitelistRepository) {
        this.userFinder = userFinder;
        this.whitelistFinder = whitelistFinder;
        this.whitelistRepository = whitelistRepository;
    }

    public void create(Cid cid) throws EntityAlreadyExistsException {
        if (this.whitelistFinder.cidIsWhitelisted(cid)) {
            throw new EntityAlreadyExistsException();
        }

        Whitelist whitelist = new Whitelist(cid);
        this.whitelistRepository.save(whitelist);
    }

    public void delete(Cid cid) throws EntityNotFoundException {
        this.whitelistRepository.deleteById(cid);
    }

}
