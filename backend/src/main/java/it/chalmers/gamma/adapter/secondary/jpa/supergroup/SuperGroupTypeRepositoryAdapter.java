package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperGroupTypeRepositoryAdapter implements SuperGroupTypeRepository {

    private static final PersistenceErrorState typeIsUsed = new PersistenceErrorState(
            "fkit_super_group_super_group_type_name_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private final SuperGroupTypeJpaRepository repository;

    public SuperGroupTypeRepositoryAdapter(SuperGroupTypeJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException {
        if (this.repository.existsById(superGroupType.value())) {
            throw new SuperGroupTypeAlreadyExistsException(superGroupType.value());
        }

        this.repository.saveAndFlush(new SuperGroupTypeEntity(superGroupType));
    }

    @Override
    public void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException {
        try {
            this.repository.deleteById(superGroupType.value());
            this.repository.flush();
        } catch (EmptyResultDataAccessException e) {
            throw new SuperGroupTypeNotFoundException();
        } catch (DataIntegrityViolationException e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(typeIsUsed)) {
                throw new SuperGroupTypeHasUsagesException();
            }

            throw e;
        }
    }

    @Override
    public List<SuperGroupType> getAll() {
        return this.repository.findAll()
                .stream()
                .map(SuperGroupTypeEntity::getId)
                .map(SuperGroupType::new)
                .toList();
    }
}
