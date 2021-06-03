package it.chalmers.gamma.internal.supergroup.service;

import it.chalmers.gamma.domain.SuperGroupId;
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

    public void create(SuperGroupDTO superGroupDTO) throws SuperGroupNotFoundException {
        this.repository.save(new SuperGroupEntity(superGroupDTO));
    }

    public void delete(SuperGroupId id) {
        this.repository.deleteById(id);
    }

    public void update(SuperGroupDTO newSuperGroup) throws SuperGroupNotFoundException {
        SuperGroupEntity superGroup = this.getEntity(newSuperGroup.id());
        superGroup.apply(newSuperGroup);
        this.repository.save(superGroup);
    }

    public List<SuperGroupDTO> getAll() {
        return Optional.of(this.repository.findAll().stream()
                .map(SuperGroupEntity::toDTO)
                .collect(Collectors.toList())).orElseThrow();
    }

    public boolean exists(SuperGroupId id) {
        return this.repository.existsById(id);
    }

    public SuperGroupDTO get(SuperGroupId id) throws SuperGroupNotFoundException {
        return getEntity(id).toDTO();
    }

    protected SuperGroupEntity getEntity(SuperGroupId id) throws SuperGroupNotFoundException {
        return this.repository.findById(id)
                .orElseThrow(SuperGroupNotFoundException::new);
    }

    public static class SuperGroupNotFoundException extends Exception { }

}
