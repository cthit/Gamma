package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.db.repository.TextRepository;
import it.chalmers.gamma.domain.GroupType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FKITService {

    private final FKITGroupRepository repo;


    public FKITService(FKITGroupRepository repo, TextRepository textRepository) {
        this.repo = repo;
    }

    public FKITGroup createGroup(String name, String prettyName, Text description, String email, GroupType type, Text function, String avatarURL) {
        if(description == null){
            description = new Text();
        }
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(name.toLowerCase());
        fkitGroup.setFunc(function);
        fkitGroup.setDescription(description);
        return saveGroup(fkitGroup, prettyName, description, email, type, function, avatarURL);
    }

    public FKITGroup editGroup(UUID id, String prettyName, Text description, String email, GroupType type, Text function, String avatarURL){ //TODO if no info, don't change value.
        FKITGroup group = repo.findById(id).orElse(null);
        if(group == null){
            return null;
        }
        group.setSVFunction(function != null ? function.getSv() : group.getSVFunction());
        group.setENFunction(function != null ? function.getEn() : group.getENFunction());
        function = group.getFunc();
        if(description != null) {
            if (group.getDescription() != null) {
                group.setSVDescription(description.getSv());
                group.setENDescription(description.getEn());
            }
        }
        return saveGroup(group, prettyName, description, email, type, function, avatarURL);
    }

    private FKITGroup saveGroup(FKITGroup group, String prettyName, Text description,
                                String email, GroupType type, Text function, String avatarURL){
        group.setPrettyName(prettyName != null ? function.getSv() : group.getSVFunction());
        group.setENDescription((description != null ? description.getEn() : group.getENDescription()));
        group.setEmail(email != null ? email : group.getEmail());
        group.setType(type != null ? type : group.getType());
        group.setAvatarURL(avatarURL != null ? avatarURL : group.getAvatarURL());
        return repo.save(group);
    }
    public boolean groupExists(String name){
        return repo.existsFKITGroupByName(name);
    }
    public boolean groupExists(UUID id){
        return repo.existsById(id);
    }
    public FKITGroup getGroup(String group){
        return repo.findByName(group);
    }
    public void removeGroup(String group){
        repo.delete(repo.findByName(group));
    }
    public void removeGroup(UUID groupId){
        repo.deleteById(groupId);
    }

    public List<FKITGroup> getGroups() {
        return repo.findAll();
    }

    public FKITGroup getGroup(UUID id){
        return repo.findById(id).orElse(null);
    }
}
