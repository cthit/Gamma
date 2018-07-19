package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.db.repository.TextRepository;
import it.chalmers.gamma.domain.GroupType;
import org.springframework.stereotype.Service;

@Service
public class FKITService {

    private final FKITGroupRepository repo;

    private final TextRepository textRepository;

    public FKITService(FKITGroupRepository repo, TextRepository textRepository) {
        this.repo = repo;
        this.textRepository = textRepository;
    }

    public FKITGroup createGroup(String name, Text description, String email, GroupType type, Text function) {
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(name);
        fkitGroup.setDescription(description);
        fkitGroup.setEmail(email);
        fkitGroup.setType(type);
        fkitGroup.setFunction(function);
        if(description != null) {
            textRepository.save(description);
        }
        textRepository.save(function);
        return repo.save(fkitGroup);
    }
    public boolean groupExists(String name){
        return repo.existsFKITGroupByName(name);
    }
    public FKITGroup getGroupInfo(String group){
        return repo.findByName(group);
    }
}
