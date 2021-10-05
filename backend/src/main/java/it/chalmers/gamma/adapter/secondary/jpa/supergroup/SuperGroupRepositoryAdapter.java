package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.port.repository.SuperGroupRepository;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuperGroupRepositoryAdapter implements SuperGroupRepository {

    private final SuperGroupJpaRepository repository;

    public SuperGroupRepositoryAdapter(SuperGroupJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(SuperGroup superGroup) throws SuperGroupAlreadyExistsException {
        this.repository.save(new SuperGroupEntity(superGroup));
    }

    @Override
    public void save(SuperGroup superGroup) throws SuperGroupNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SuperGroup> getAll() {
        return this.repository.findAll().stream().map(SuperGroupEntity::toDomain).toList();
    }

    @Override
    public List<SuperGroup> getAllByType(SuperGroupType superGroupType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SuperGroup> get(SuperGroupId superGroupId) {
        return this.repository.findById(superGroupId.value()).map(SuperGroupEntity::toDomain);
    }
}
