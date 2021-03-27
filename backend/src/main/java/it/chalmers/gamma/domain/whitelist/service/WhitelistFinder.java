package it.chalmers.gamma.domain.whitelist.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.domain.whitelist.data.Whitelist;
import it.chalmers.gamma.domain.whitelist.data.WhitelistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WhitelistFinder implements GetAllEntities<Cid> {

    private final WhitelistRepository repository;

    public WhitelistFinder(WhitelistRepository repository) {
        this.repository = repository;
    }

    public boolean cidIsWhitelisted(Cid cid) {
        return this.repository.existsById(cid);
    }

    public List<Cid> getAll() {
        return this.repository.findAll()
                .stream()
                .map(Whitelist::getCid)
                .collect(Collectors.toList());
    }

}
