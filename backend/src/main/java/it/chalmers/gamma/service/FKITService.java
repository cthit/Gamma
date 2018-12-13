package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.requests.CreateGroupRequest;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class FKITService {

    private final FKITGroupRepository repo;

    public FKITService(FKITGroupRepository repo) {
        this.repo = repo;
    }

    public FKITGroup createGroup(CreateGroupRequest request) {
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setSuperGroup(request.getSuperGroup());
        fkitGroup.setName(request.getName().toLowerCase());
        fkitGroup.setFunc(request.getFunc());
        fkitGroup.setDescription(request.getDescription());
        return saveGroup(fkitGroup, request.getPrettyName() == null ? request.getName() : request.getPrettyName(),
                request.getDescription() == null ? new Text() : request.getDescription(),
                request.getEmail(), request.getAvatarURL());
    }

    //TODO if no info, don't change value.
    public FKITGroup editGroup(UUID id, CreateGroupRequest request) {
        FKITGroup group = this.repo.findById(id).orElse(null);
        if (group == null) {
            return null;
        }
        group.setSVFunction(request.getFunc() == null ? group.getSVFunction() : request.getFunc().getSv());
        group.setENFunction(request.getFunc() == null ? group.getENFunction() : request.getFunc().getEn());
        if (request.getDescription() != null && group.getDescription() != null) {
            group.setSVDescription(request.getDescription().getSv());
            group.setENDescription(request.getDescription().getEn());
        }
        return saveGroup(group, request.getPrettyName(), request.getDescription(), request.getEmail(),
                request.getAvatarURL());
    }

    private FKITGroup saveGroup(FKITGroup group, String prettyName, Text description,
                                String email, String avatarURL) {
        group.setPrettyName(prettyName == null ? group.getPrettyName() : prettyName);
        group.setSVDescription(description == null ? group.getSVDescription() : description.getSv());
        group.setENDescription(description == null ? group.getENDescription() : description.getEn());
        group.setEmail(email == null ? group.getEmail() : email);
        group.setAvatarURL(avatarURL == null ? group.getAvatarURL() : avatarURL);

        return this.repo.save(group);
    }

    public boolean groupExists(String name) {
        return this.repo.existsFKITGroupByName(name);
    }

    public boolean groupExists(UUID id) {
        return this.repo.existsById(id);
    }

    public void removeGroup(String group) {
        this.repo.delete(this.repo.findByName(group));
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public List<FKITGroup> getGroups() {
        return this.repo.findAll();
    }

    public FKITGroup getGroup(String group) {
        return this.repo.findByName(group);
    }

    public FKITGroup getGroup(UUID id) {
        return this.repo.findById(id).orElse(null);
    }

}
