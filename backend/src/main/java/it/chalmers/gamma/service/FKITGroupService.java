package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FKITGroupService {

    private final FKITGroupRepository repo;

    public FKITGroupService(FKITGroupRepository repo) {
        this.repo = repo;
    }

    public boolean groupExists(String name) {
        return this.repo.existsFKITGroupByName(name);
    }

    public boolean groupExists(UUID id) {
        return this.repo.existsById(id);
    }

    public void removeGroup(String name) {
        this.repo.deleteByName(name);
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public List<FKITGroupDTO> getGroups() {
        return this.repo.findAll().stream().map(FKITGroup::toDTO).collect(Collectors.toList());
    }

    public FKITGroupDTO getDTOGroup(String name) {
        return this.repo.findByName(name).map(FKITGroup::toDTO).orElse(null);
    }

    public FKITGroupDTO getDTOGroup(UUID id) {
        return this.repo.findById(id).map(FKITGroup::toDTO).orElse(null);
    }

    public void editGroupAvatar(FKITGroupDTO group, String url) {
        group.setAvatarURL(url);
        this.repo.save(group);
    }

    public void save(FKITGroup group) {
        this.repo.save(group);
    }

    protected FKITGroup getGroup(FKITGroupDTO group) {
        return this.repo.findById(group.getId()).orElse(null);
    }

}
