package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WhitelistService {
    private final WhitelistRepository whitelistRepository;

    private WhitelistService(WhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    /**
     * adds a cid that can create a account
     * @param cid the cid that is added to the whitelisted database
     * @return a copy of the whitelist object that is created
     */
    public Whitelist addWhiteListedCID(String cid) {
        Whitelist whitelistedCID = new Whitelist(cid);
        return whitelistRepository.save(whitelistedCID);
    }

    public void removeWhiteListedCID(String cid) {
        whitelistRepository.delete(whitelistRepository.findByCid(cid));
    }

    /**
     * gets whitelist object by cid
     * @param cid the cid thats searched for
     * @return if found the whitelist object searched for, otherwise null
     */
    public Whitelist getWhitelist(String cid) {
        return whitelistRepository.findByCid(cid);
    }

    /**
     * checks if cid has been whitelisted
     * @param cid the cid check if whitelisted
     * @return true if exists in the database, false otherwise
     */
    public boolean isCIDWhiteListed(String cid) {
        if (whitelistRepository.findByCid(cid) == null) {
            return false;
        }
        return true;
    }
    public List<Whitelist> getAllWhitelist(){
        return whitelistRepository.findAll();
    }

    /**
     * gets whitelist object by id
     * @param id the ID of the whitelist object to get
     * @return the whitelist object that has corresponding ID
     */
    public Whitelist getWhitelistById(String id){
        Optional<Whitelist> whitelist = whitelistRepository.findById(UUID.fromString(id));
        return whitelist.orElse(null);
    }

    /**
     * edits the cid of a already whitelisted object
     * @param whitelist the old whitelist object that should be edited
     * @param newCid the new cid that will replace the old whitelisted cid
     */
    public void editWhitelist(Whitelist whitelist, String newCid){
        whitelist.setCid(newCid);
        whitelistRepository.save(whitelist);
    }
}
