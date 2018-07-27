package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.db.repository.TextRepository;
import it.chalmers.gamma.domain.GroupType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FKITService {

    private final FKITGroupRepository repo;


    public FKITService(FKITGroupRepository repo, TextRepository textRepository) {
        this.repo = repo;
    }

    public FKITGroup createGroup(String name, Text description, String email, GroupType type, Text function, String avatarURL) {
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(name.toLowerCase());
        return saveGroup(fkitGroup, name, description, email, type, function, avatarURL);
    }

    public FKITGroup editGroup(String name, Text description, String email, GroupType type, Text function, String avatarURL){ //TODO if no info, don't change value.
        FKITGroup group = repo.findByName(name.toLowerCase());
        group.setSVFunction(function.getSv());
        group.setENFunction(function.getEn());
        function = group.getFunc();
        if(description != null) {
            if (group.getDescription() != null) {
                group.setSVDescription(description.getSv());
                group.setENDescription(description.getEn());
            }
        }
        return saveGroup(group, name, description, email, type, function, avatarURL);
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
    public FKITGroup getGroup(String group){
        return repo.findByName(group);
    }
    public void removeGroup(String group){
        repo.delete(repo.findByName(group));
    }

    public List<FKITGroup> getGroups() {
        return repo.findAll();
    }
}
