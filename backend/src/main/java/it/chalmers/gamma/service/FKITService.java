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

    public FKITGroup createGroup(String name, Text description, String email, GroupType type, Text function, String avatarURL) {
        if(description == null){
            description = new Text();
        }
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(name.toLowerCase());
        return saveGroup(fkitGroup, name, description, email, type, function, avatarURL);
    }

    public FKITGroup editGroup(UUID id, Text description, String email, GroupType type, Text function, String avatarURL){ //TODO if no info, don't change value.
        FKITGroup group = repo.findById(id).orElse(null);
        if(group == null){
            return null;
        }
        group.setSVFunction(function.getSv());
        group.setENFunction(function.getEn());
        function = group.getFunc();
        if(description != null) {
            if (group.getDescription() != null) {
                group.setSVDescription(description.getSv());
                group.setENDescription(description.getEn());
            }
        }
        return saveGroup(group, group.getName(), description, email, type, function, avatarURL);
    }

    private FKITGroup saveGroup(FKITGroup fkitGroup, String name, Text description,
                                String email, GroupType type, Text function, String avatarURL){
        fkitGroup.setPrettyName(name);
        fkitGroup.setDescription(description);
        fkitGroup.setEmail(email);
        fkitGroup.setType(type);
        fkitGroup.setFunc(function);
        fkitGroup.setAvatarURL(avatarURL);
        return repo.save(fkitGroup);
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

    public FKITGroup getGroupById(UUID id){
        return repo.findById(id).orElse(null);
    }
}
