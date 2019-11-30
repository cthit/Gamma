package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.repository.FKITSuperGroupRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FKITSuperGroupService {
    private final FKITSuperGroupRepository repository;

    public FKITSuperGroupService(FKITSuperGroupRepository repository) {
        this.repository = repository;
    }

    public FKITSuperGroupDTO createSuperGroup(CreateSuperGroupRequest request) {
        FKITSuperGroup group = new FKITSuperGroup();
        group.setName(request.getName());
        group.setPrettyName(request.getPrettyName() == null ? request.getName() : request.getPrettyName());
        group.setType(request.getType());
        group.setEmail(request.getEmail());
        return this.repository.save(group).toDTO();
    }

    public FKITSuperGroupDTO getGroupDTO(UUID id) {
        return this.repository.getById(id).toDTO();
    }

    public boolean groupExists(String name) {
        return this.repository.existsByName(name);
    }
    public boolean groupExists(UUID id) {
        return this.repository.existsById(id);
    }

    public void removeGroup(UUID id) {
        this.repository.deleteById(id);
    }
    public List<FKITSuperGroupDTO> getAllGroups() {
        return this.repository.findAll().stream().map(FKITSuperGroup::toDTO).collect(Collectors.toList());
    }

    public void updateSuperGroup(UUID id, CreateSuperGroupRequest request) {
        FKITSuperGroup group = this.repository.getById(id);
        group.setType(request.getType() == null ? group.getType() : request.getType());
        group.setName(request.getName() == null ? group.getName() : request.getName());
        group.setPrettyName(request.getPrettyName() == null ? group.getPrettyName() : request.getPrettyName());
        group.setEmail(request.getEmail() == null ? group.getEmail() : request.getEmail());
        this.repository.save(group);
    }

    protected FKITSuperGroup getGroup(FKITSuperGroupDTO group) {
        return this.repository.getById(group.getId());
    }
}
