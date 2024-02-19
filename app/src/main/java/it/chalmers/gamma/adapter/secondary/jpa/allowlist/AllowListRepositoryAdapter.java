package it.chalmers.gamma.adapter.secondary.jpa.allowlist;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import it.chalmers.gamma.app.user.domain.Cid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllowListRepositoryAdapter implements AllowListRepository {

    private final AllowListJpaRepository allowListJpaRepository;

    private final PersistenceErrorState CID_ALREADY_ALLOWED = new PersistenceErrorState(
            "g_allow_list_pkey", PersistenceErrorState.Type.NOT_UNIQUE
    );

    public AllowListRepositoryAdapter(AllowListJpaRepository allowListJpaRepository) {
        this.allowListJpaRepository = allowListJpaRepository;
    }

    @Override
    public void allow(Cid cid) throws AlreadyAllowedException {
        try {
            this.allowListJpaRepository.save(new AllowListEntity(cid.value()));
        } catch (DataIntegrityViolationException e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (CID_ALREADY_ALLOWED.equals(state)) {
                throw new AlreadyAllowedException();
            }
        }
    }

    @Override
    public void remove(Cid cid) throws NotOnAllowListException {
        try {
            this.allowListJpaRepository.deleteById(cid.value());
        } catch(EmptyResultDataAccessException e) {
            throw new NotOnAllowListException();
        }
    }

    @Override
    public boolean isAllowed(Cid cid) {
        return this.allowListJpaRepository.existsById(cid.value());
    }

    @Override
    public List<Cid> getAllowList() {
        return this.allowListJpaRepository.findAll()
                .stream()
                .map(AllowListEntity::getId)
                .map(Cid::new)
                .toList();
    }
}
