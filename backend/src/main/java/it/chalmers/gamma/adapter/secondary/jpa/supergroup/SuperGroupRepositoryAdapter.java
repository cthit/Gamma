package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    }

    @Override
    public void save(SuperGroup superGroup) throws SuperGroupNotFoundException {

    }

    @Override
    public void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException {

    }

    @Override
    public List<SuperGroup> getAll() {
        return this.repository.findAll().stream().map(SuperGroupEntity::toDomain).toList();
    }

    @Override
    public List<SuperGroup> getAllByType(SuperGroupType superGroupType) {
        return Collections.emptyList();
    }

    @Override
    public Optional<SuperGroup> get(SuperGroupId superGroupId) {
        return Optional.empty();
    }
}
