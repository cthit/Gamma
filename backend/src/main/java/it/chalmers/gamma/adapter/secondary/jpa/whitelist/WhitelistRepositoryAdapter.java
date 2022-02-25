package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.whitelist.WhitelistRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhitelistRepositoryAdapter implements WhitelistRepository {

    private final WhitelistJpaRepository whitelistJpaRepository;

    private final PersistenceErrorState CID_ALREADY_WHITELISTED = new PersistenceErrorState(
            null, PersistenceErrorState.Type.NOT_UNIQUE
    );

    public WhitelistRepositoryAdapter(WhitelistJpaRepository whitelistJpaRepository) {
        this.whitelistJpaRepository = whitelistJpaRepository;
    }

    @Override
    public void whitelist(Cid cid) throws AlreadyWhitelistedException {
        try {
            this.whitelistJpaRepository.save(new WhitelistEntity(cid.value()));
        } catch (DataIntegrityViolationException e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (CID_ALREADY_WHITELISTED.equals(state)) {
                throw new AlreadyWhitelistedException();
            }
        }
    }

    @Override
    public void remove(Cid cid) throws NotWhitelistedException {
        try{
            this.whitelistJpaRepository.deleteById(cid.value());
            this.whitelistJpaRepository.flush();
        } catch (EmptyResultDataAccessException e) {
            throw new NotWhitelistedException();
        }
    }

    @Override
    public boolean isWhitelisted(Cid cid) {
        return this.whitelistJpaRepository.existsById(cid.value());
    }

    @Override
    public List<Cid> getWhitelist() {
        return this.whitelistJpaRepository.findAll()
                .stream()
                .map(WhitelistEntity::getId)
                .toList();
    }
}
