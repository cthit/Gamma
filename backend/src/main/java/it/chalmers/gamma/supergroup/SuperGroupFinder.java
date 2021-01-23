package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.GroupType;
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

    public Optional<SuperGroupDTO> getSuperGroup(UUID id) {
        Optional<SuperGroup> superGroupEntity = getSuperGroupEntity(id);
        Optional<SuperGroupDTO> superGroup = Optional.empty();

        if(superGroupEntity.isPresent()) {
            superGroup = superGroupEntity.map(this::toDTO);
        }

        return superGroup;
    }

    protected Optional<SuperGroup> getSuperGroupEntity(UUID id) {
        return this.superGroupRepository.findById(id);
    }

    //TODO: Remove
    public SuperGroupDTO getGroupDTO(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.superGroupRepository.findById(UUID.fromString(id))
                    .orElseThrow(SuperGroupDoesNotExistResponse::new).toDTO();
        }
        return this.superGroupRepository.findByName(id.toLowerCase())
                .orElseThrow(SuperGroupDoesNotExistResponse::new).toDTO();
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
