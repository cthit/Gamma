package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.domain.GroupType;
import org.springframework.stereotype.Service;

@Service
public class FKITService {

    private final FKITGroupRepository repo;

    public FKITService(FKITGroupRepository repo) {
        this.repo = repo;
    }

    public FKITGroup createGroup(String name, String description, String email, GroupType type) {
        FKITGroup fkitGroup = new FKITGroup();
        return repo.save(fkitGroup);
    }
    public boolean groupExists(String name){
        return repo.existsFKITGroupByName(name);
    }
    public FKITGroup getGroupInfo(String group){
        return repo.findByName(group);
    }
}
