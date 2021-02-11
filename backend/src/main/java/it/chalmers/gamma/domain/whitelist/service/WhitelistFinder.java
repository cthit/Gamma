package it.chalmers.gamma.domain.whitelist.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.whitelist.data.WhitelistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WhitelistFinder {

    private final WhitelistRepository repository;

    public WhitelistFinder(WhitelistRepository repository) {
        this.repository = repository;
    }

    public boolean cidIsWhitelisted(Cid cid) {
        return this.repository.existsById(cid.value);
    }

    public List<Cid> getWhitelist() {
        return this.repository.findAll()
                .stream()
                .map(whitelist -> new Cid(whitelist.getCid()))
                .collect(Collectors.toList());
    }

}
