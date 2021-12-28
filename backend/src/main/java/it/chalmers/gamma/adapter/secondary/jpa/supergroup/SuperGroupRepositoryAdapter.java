package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuperGroupRepositoryAdapter implements SuperGroupRepository {

    private final SuperGroupJpaRepository repository;
    private final SuperGroupEntityConverter superGroupEntityConverter;

    public SuperGroupRepositoryAdapter(SuperGroupJpaRepository repository,
                                       SuperGroupEntityConverter superGroupEntityConverter) {
        this.repository = repository;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    @Override
    public void save(SuperGroup superGroup) {
        this.repository.saveAndFlush(this.superGroupEntityConverter.toEntity(superGroup));
    }

    @Override
    public void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException {
        this.repository.deleteById(superGroupId.value());
    }

    @Override
    public List<SuperGroup> getAll() {
        return this.repository.findAll().stream().map(this.superGroupEntityConverter::toDomain).toList();
    }

    @Override
    public List<SuperGroup> getAllByType(SuperGroupType superGroupType) {
        return this.repository.findAllBySuperGroupType(superGroupType.value())
                .stream()
                .map(this.superGroupEntityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<SuperGroup> get(SuperGroupId superGroupId) {
        return this.repository.findById(superGroupId.value()).map(this.superGroupEntityConverter::toDomain);
    }
}
