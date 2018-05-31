package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhitelistService {
    private final WhitelistRepository whitelistRepository;

    private WhitelistService(WhitelistRepository whitelistRepository){
        this.whitelistRepository = whitelistRepository;
    }

    public List<Whitelist> findAll(){
        return whitelistRepository.findAll();
    }
    public Whitelist addWhiteListedCID(String cid){
        Whitelist whitelistedCID = new Whitelist(cid);

        return whitelistRepository.save(whitelistedCID);
    }
}
