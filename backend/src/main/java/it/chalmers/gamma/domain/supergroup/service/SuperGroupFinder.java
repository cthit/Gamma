package it.chalmers.gamma.domain.supergroup.service;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.data.SuperGroup;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupRepository;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SuperGroupFinder {

    private final SuperGroupRepository superGroupRepository;

    public SuperGroupFinder(SuperGroupRepository superGroupRepository) {
        this.superGroupRepository = superGroupRepository;
    }

    public boolean superGroupExistsByName(String name) {
        return this.superGroupRepository.existsByName(name);
    }

    public boolean superGroupExists(SuperGroupId id) {
        return this.superGroupRepository.existsById(id);
    }

    public SuperGroupDTO getSuperGroup(SuperGroupId id) throws SuperGroupNotFoundException {
        return toDTO(getSuperGroupEntity(id));
    }

    public SuperGroupDTO getSuperGroupByName(String name) throws SuperGroupNotFoundException {
        return toDTO(getSuperGroupEntityByName(name));
    }

    protected SuperGroup getSuperGroupEntity(SuperGroupId id) throws SuperGroupNotFoundException {
        return this.superGroupRepository.findById(id).orElseThrow(SuperGroupNotFoundException::new);
    }

    protected SuperGroup getSuperGroupEntity(SuperGroupDTO superGroup) throws SuperGroupNotFoundException {
        return getSuperGroupEntity(superGroup.getId());
    }

    protected SuperGroup getSuperGroupEntityByName(String name) throws SuperGroupNotFoundException {
        return this.superGroupRepository.findByName(name)
                .orElseThrow(SuperGroupNotFoundException::new);
    }

    public List<SuperGroupDTO> getSuperGroups() {
        return Optional.of(this.superGroupRepository.findAll().stream()
                .filter(g -> !g.getType().equals(GroupType.ADMIN))
                .map(this::toDTO)
                .collect(Collectors.toList())).orElseThrow();
    }

    protected SuperGroupDTO toDTO(SuperGroup superGroup) {
        return new SuperGroupDTO(
                superGroup.getId(),
                superGroup.getName(),
                superGroup.getPrettyName(),
                superGroup.getType(),
                superGroup.getEmail(),
                superGroup.getDescription()
        );
    }
}
