package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.exceptions.CIDAlreadyWhitelistedException;
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
    public Whitelist addWhiteListedCID(String cid) throws CIDAlreadyWhitelistedException {
        if(isCIDWhiteListed(cid)){
            throw new CIDAlreadyWhitelistedException();
        }
        Whitelist whitelistedCID = new Whitelist(cid);
        System.out.println("white listed " + cid);
        return whitelistRepository.save(whitelistedCID);
    }
    public void removeWhiteListedCID() throws Exception{

    }
    public boolean isCIDWhiteListed(String cid){
        if(whitelistRepository.findByCid(cid) == null){
            return false;
        }
        return true;
    }
}
