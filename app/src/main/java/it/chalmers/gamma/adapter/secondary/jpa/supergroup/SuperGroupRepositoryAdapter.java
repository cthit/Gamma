package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuperGroupRepositoryAdapter implements SuperGroupRepository {

    private static final PersistenceErrorState typeNotFound = new PersistenceErrorState(
            "fkit_super_group_super_group_type_name_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private static final PersistenceErrorState nameAlreadyExists = new PersistenceErrorState(
            "fkit_super_group_e_name_key",
            PersistenceErrorState.Type.NOT_UNIQUE
    );
    private static final PersistenceErrorState superGroupIsUsed = new PersistenceErrorState(
            "fkit_group_super_group_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private final SuperGroupJpaRepository repository;
    private final SuperGroupTypeJpaRepository superGroupTypeJpaRepository;
    private final SuperGroupEntityConverter superGroupEntityConverter;

    public SuperGroupRepositoryAdapter(SuperGroupJpaRepository repository,
                                       SuperGroupTypeJpaRepository superGroupTypeJpaRepository,
                                       SuperGroupEntityConverter superGroupEntityConverter) {
        this.repository = repository;
        this.superGroupTypeJpaRepository = superGroupTypeJpaRepository;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    @Override
    public void save(SuperGroup superGroup) {
        try {
            this.repository.saveAndFlush(toEntity(superGroup));
        } catch (DataIntegrityViolationException e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(typeNotFound)) {
                throw new TypeNotFoundRuntimeException();
            } else if (state.equals(nameAlreadyExists)) {
                throw new NameAlreadyExistsRuntimeException();
            }

            throw e;
        }
    }

    @Override
    public void delete(SuperGroupId superGroupId) throws SuperGroupNotFoundException, SuperGroupIsUsedException {
        try {
            this.repository.deleteById(superGroupId.value());
        } catch (EmptyResultDataAccessException e) {
            throw new SuperGroupNotFoundException();
        } catch (Exception e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(superGroupIsUsed)) {
                throw new SuperGroupIsUsedException();
            }

            throw e;
        }
    }

    @Override
    public List<SuperGroup> getAll() {
        return this.repository.findAll().stream().map(this.superGroupEntityConverter::toDomain).toList();
    }

    @Override
    public List<SuperGroup> getAllByType(SuperGroupType superGroupType) {
        return this.repository.findAllBySuperGroupType_Name(superGroupType.value())
                .stream()
                .map(this.superGroupEntityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<SuperGroup> get(SuperGroupId superGroupId) {
        return this.repository.findById(superGroupId.value()).map(this.superGroupEntityConverter::toDomain);
    }

    private SuperGroupEntity toEntity(SuperGroup superGroup) {
        SuperGroupEntity superGroupEntity = this.repository.findById(superGroup.id().value())
                .orElse(new SuperGroupEntity());

        superGroupEntity.increaseVersion(superGroup.version());

        superGroupEntity.id = superGroup.id().value();
        superGroupEntity.superGroupType = this.superGroupTypeJpaRepository.getById(superGroup.type().value());
        superGroupEntity.name = superGroup.name().value();
        superGroupEntity.prettyName = superGroup.prettyName().value();

        if (superGroupEntity.description == null) {
            superGroupEntity.description = new TextEntity();
        }

        superGroupEntity.description.apply(superGroup.description());

        return superGroupEntity;
    }


}
