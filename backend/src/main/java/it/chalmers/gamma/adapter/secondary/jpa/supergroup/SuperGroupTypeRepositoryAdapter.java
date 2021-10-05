package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.port.repository.SuperGroupTypeRepository;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperGroupTypeRepositoryAdapter implements SuperGroupTypeRepository {

    private final SuperGroupTypeJpaRepository repository;

    public SuperGroupTypeRepositoryAdapter(SuperGroupTypeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException {
        this.repository.save(new SuperGroupTypeEntity(superGroupType));
    }

    @Override
    public void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SuperGroupType> getAll() {
        return this.repository.findAll().stream().map(SuperGroupTypeEntity::get).toList();
    }
}
