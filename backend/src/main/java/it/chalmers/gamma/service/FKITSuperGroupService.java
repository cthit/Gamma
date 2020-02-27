package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.repository.FKITSuperGroupRepository;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;

import it.chalmers.gamma.response.supergroup.SuperGroupDoesNotExistResponse;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FKITSuperGroupService {
    private final FKITSuperGroupRepository repository;

    public FKITSuperGroupService(FKITSuperGroupRepository repository) {
        this.repository = repository;
    }

    public FKITSuperGroupDTO createSuperGroup(FKITSuperGroupDTO superGroupDTO) {
        FKITSuperGroup group = new FKITSuperGroup();
        group.setName(superGroupDTO.getName());
        group.setPrettyName(superGroupDTO.getPrettyName() == null
                ? superGroupDTO.getName() : superGroupDTO.getPrettyName());
        group.setType(superGroupDTO.getType());
        group.setEmail(superGroupDTO.getEmail());
        return this.repository.save(group).toDTO();
    }

    public FKITSuperGroupDTO getGroupDTO(String id) throws SuperGroupDoesNotExistResponse {
        if (UUIDUtil.validUUID(id)) {
            return this.repository.findById(UUID.fromString(id))
                    .orElseThrow(SuperGroupDoesNotExistResponse::new).toDTO();
        }
        return this.repository.findByName(id.toLowerCase())
                .orElseThrow(SuperGroupDoesNotExistResponse::new).toDTO();
    }

    public boolean groupExists(String name) {
        return this.repository.existsByName(name.toLowerCase())
                || UUIDUtil.validUUID(name) && this.repository.existsById(UUID.fromString(name));
    }

    public void removeGroup(UUID id) {
        this.repository.deleteById(id);
    }

    public List<FKITSuperGroupDTO> getAllGroups() {
        return Optional.of(this.repository.findAll().stream()
                .filter(g -> !g.getType().equals(GroupType.ADMIN))
                .map(FKITSuperGroup::toDTO)
                .collect(Collectors.toList())).orElseThrow();
    }

    public void updateSuperGroup(UUID id, FKITSuperGroupDTO superGroupDTO) {
        FKITSuperGroup group = this.getGroup(this.getGroupDTO(id.toString()));
        group.setType(superGroupDTO.getType() == null ? group.getType() : superGroupDTO.getType());
        group.setName(superGroupDTO.getName() == null ? group.getName() : superGroupDTO.getName());
        group.setPrettyName(superGroupDTO.getPrettyName() == null
                ? group.getPrettyName() : superGroupDTO.getPrettyName());
        group.setEmail(superGroupDTO.getEmail() == null ? group.getEmail() : superGroupDTO.getEmail());
        this.repository.save(group);
    }

    protected FKITSuperGroup getGroup(FKITSuperGroupDTO group) {
        return this.repository.findById(group.getId())
                .orElse(null);
    }
}
