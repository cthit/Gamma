package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuperGroupService {

    private final SuperGroupRepository repository;

    public SuperGroupService(SuperGroupRepository repository) {
        this.repository = repository;
    }

    public void create(SuperGroup superGroup) throws SuperGroupNotFoundException {
        this.repository.save(new SuperGroupEntity(superGroup));
    }

    public void delete(SuperGroupId id) {
        this.repository.deleteById(id);
    }

    public void update(SuperGroup newSuperGroup) throws SuperGroupNotFoundException {
        SuperGroupEntity superGroup = this.getEntity(newSuperGroup.id());
        superGroup.apply(newSuperGroup);
        this.repository.save(superGroup);
    }

    public List<SuperGroup> getAll() {
        return Optional.of(this.repository.findAll().stream()
                .map(SuperGroupEntity::toDTO)
                .collect(Collectors.toList())).orElseThrow();
    }

    public boolean exists(SuperGroupId id) {
        return this.repository.existsById(id);
    }

    public SuperGroup get(SuperGroupId id) throws SuperGroupNotFoundException {
        return getEntity(id).toDTO();
    }

    public List<SuperGroup> getAllByType(SuperGroupType type) {
        return this.repository.findAllBySuperGroupType(type)
                .stream()
                .map(SuperGroupEntity::toDTO)
                .collect(Collectors.toList());
    }

    protected SuperGroupEntity getEntity(SuperGroupId id) throws SuperGroupNotFoundException {
        return this.repository.findById(id)
                .orElseThrow(SuperGroupNotFoundException::new);
    }

    public static class SuperGroupNotFoundException extends Exception { }

}
