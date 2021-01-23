package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.GroupType;

import it.chalmers.gamma.supergroup.response.SuperGroupDoesNotExistResponse;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class SuperGroupService {
    private final SuperGroupRepository repository;

    public SuperGroupService(SuperGroupRepository repository) {
        this.repository = repository;
    }

    public SuperGroupDTO createSuperGroup(SuperGroupDTO superGroupDTO) {
        SuperGroup group = new SuperGroup();
        UUID id = superGroupDTO.getId();
        if (id == null) {
            id = UUID.randomUUID();
        }
        group.setId(id);
        group.setName(superGroupDTO.getName());
        group.setPrettyName(superGroupDTO.getPrettyName() == null
                ? superGroupDTO.getName() : superGroupDTO.getPrettyName());
        group.setType(superGroupDTO.getType());
        group.setEmail(superGroupDTO.getEmail());
        return this.repository.save(group).toDTO();
    }


    public void removeGroup(UUID id) {
        this.repository.deleteById(id);
    }

    public void updateSuperGroup(UUID id, SuperGroupDTO superGroupDTO) {
        SuperGroup group = this.fromDTO(this.getGroupDTO(id.toString()));
        group.setType(superGroupDTO.getType() == null ? group.getType() : superGroupDTO.getType());
        group.setName(superGroupDTO.getName() == null ? group.getName() : superGroupDTO.getName());
        group.setPrettyName(superGroupDTO.getPrettyName() == null
                ? group.getPrettyName() : superGroupDTO.getPrettyName());
        group.setEmail(superGroupDTO.getEmail() == null ? group.getEmail() : superGroupDTO.getEmail());
        this.repository.save(group);
    }
}
