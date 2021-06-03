package it.chalmers.gamma.internal.whitelist.service;

import it.chalmers.gamma.domain.Cid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WhitelistService {

    private final WhitelistRepository repository;

    public WhitelistService(WhitelistRepository repository) {
        this.repository = repository;
    }

    public void create(Cid cid) throws WhitelistNotFoundException {
        if (this.cidIsWhitelisted(cid)) {
            throw new WhitelistNotFoundException();
        }

        WhitelistEntity whitelist = new WhitelistEntity(cid);
        this.repository.save(whitelist);
    }

    public void delete(Cid cid) throws WhitelistNotFoundException {
        try{
            this.repository.deleteById(cid);
        } catch(IllegalArgumentException e) {
            throw new WhitelistNotFoundException();
        }
    }

    public boolean cidIsWhitelisted(Cid cid) {
        return this.repository.existsById(cid);
    }

    public List<Cid> getAll() {
        return this.repository.findAll()
                .stream()
                .map(WhitelistEntity::get)
                .collect(Collectors.toList());
    }

    public static class WhitelistNotFoundException extends Exception { }

}
