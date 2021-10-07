package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.app.port.repository.WhitelistRepository;
import it.chalmers.gamma.app.domain.user.Cid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhitelistRepositoryAdapter implements WhitelistRepository {

    private final WhitelistJpaRepository whitelistJpaRepository;

    public WhitelistRepositoryAdapter(WhitelistJpaRepository whitelistJpaRepository) {
        this.whitelistJpaRepository = whitelistJpaRepository;
    }

    @Override
    public void whitelist(Cid cid) throws AlreadyWhitelistedException {
        this.whitelistJpaRepository.save(new WhitelistEntity(cid.value()));
    }

    @Override
    public void remove(Cid cid) throws CidIsNotWhitelistedException {
        this.whitelistJpaRepository.deleteById(cid.value());
    }

    @Override
    public boolean isWhitelisted(Cid cid) {
        return this.whitelistJpaRepository.existsById(cid.value());
    }

    @Override
    public List<Cid> getWhitelist() {
        return this.whitelistJpaRepository.findAll()
                .stream()
                .map(WhitelistEntity::get)
                .toList();
    }
}
