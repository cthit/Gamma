package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.domain.GroupType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class FKITService {

    private final FKITGroupRepository repo;

    public FKITService(FKITGroupRepository repo) {
        this.repo = repo;
    }

    public FKITGroup createGroup(String name, String prettyName, Text description, String email,
                                 GroupType type, Text function, String avatarURL) {
        if (description == null) {
            description = new Text();
        }
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(name.toLowerCase());
        fkitGroup.setFunc(function);
        fkitGroup.setDescription(description);
        return saveGroup(fkitGroup, prettyName == null ? name : prettyName, description,
            email, type, function, avatarURL);
    }

    //TODO if no info, don't change value.
    public FKITGroup editGroup(UUID id, String prettyName, Text description, String email,
                               GroupType type, Text function, String avatarURL) {
        FKITGroup group = this.repo.findById(id).orElse(null);
        if (group == null) {
            return null;
        }
        group.setSVFunction(function == null ? group.getSVFunction() : function.getSv());
        group.setENFunction(function == null ? group.getENFunction() : function.getEn());
        function = group.getFunc();
        if (description != null && group.getDescription() != null) {
            group.setSVDescription(description.getSv());
            group.setENDescription(description.getEn());
        }
        return saveGroup(group, prettyName, description, email, type, function, avatarURL);
    }

    private FKITGroup saveGroup(FKITGroup group, String prettyName, Text description,
                                String email, GroupType type, Text function, String avatarURL) {
        group.setPrettyName(prettyName == null ? group.getPrettyName() : prettyName);
        group.setENDescription(description == null ? group.getENDescription() : description.getEn());
        group.setEmail(email == null ? group.getEmail() : email);
        group.setType(type == null ? group.getType() : type);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITService that = (FKITService) o;
        return this.repo.equals(that.repo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repo);
    }

    @Override
    public String toString() {
        return "FKITService{"
            + "repo=" + this.repo
            + '}';
    }
}
