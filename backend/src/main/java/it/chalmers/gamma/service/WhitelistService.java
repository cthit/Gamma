package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService {
    private final WhitelistRepository whitelistRepository;

    private WhitelistService(WhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    public Whitelist addWhiteListedCID(String cid) {
        Whitelist whitelistedCID = new Whitelist(cid);
        System.out.println("white listed " + cid);
        return whitelistRepository.save(whitelistedCID);
    }

    //TODO
    public void removeWhiteListedCID(String cid) {
        whitelistRepository.delete(whitelistRepository.findByCid(cid));
    }

    public Whitelist getWhitelist(String cid) {
        return whitelistRepository.findByCid(cid);
    }

    public boolean isCIDWhiteListed(String cid) {
        if (whitelistRepository.findByCid(cid) == null) {
            return false;
        }
        return true;
    }
}
