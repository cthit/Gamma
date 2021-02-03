package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.supergroup.response.SuperGroupDoesNotExistResponse;
import it.chalmers.gamma.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SuperGroupFinder {

    private SuperGroupRepository superGroupRepository;

    public SuperGroupFinder(SuperGroupRepository superGroupRepository) {
        this.superGroupRepository = superGroupRepository;
    }

    public SuperGroupDTO getSuperGroup(UUID id) throws SuperGroupNotFoundException {
        return toDTO(getSuperGroupEntity(id));
    }

    protected SuperGroup getSuperGroupEntity(UUID id) throws SuperGroupNotFoundException {
        return this.superGroupRepository.findById(id).orElseThrow(SuperGroupNotFoundException::new);
    }

    public SuperGroup getSuperGroupEntity(SuperGroupDTO superGroup) throws SuperGroupNotFoundException {
        return getSuperGroupEntity(superGroup.getId());
    }

    public List<SuperGroupDTO> getAllGroups() {
        return Optional.of(this.superGroupRepository.findAll().stream()
                .filter(g -> !g.getType().equals(GroupType.ADMIN))
                .map(SuperGroup::toDTO)
                .collect(Collectors.toList())).orElseThrow();
    }

    protected SuperGroupDTO toDTO(SuperGroup superGroup) {
        return new SuperGroupDTO(
                superGroup.getId(),
                superGroup.getName(),
                superGroup.getPrettyName(),
                superGroup.getType(),
                superGroup.getEmail()
        );
    }
}
