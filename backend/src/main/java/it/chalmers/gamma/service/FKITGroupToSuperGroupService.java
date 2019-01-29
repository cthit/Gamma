package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.pk.FKITGroupToSuperGroupPK;
import it.chalmers.gamma.db.repository.FKITGroupToSuperGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FKITGroupToSuperGroupService {
    private final FKITGroupToSuperGroupRepository repository;

    public FKITGroupToSuperGroupService(FKITGroupToSuperGroupRepository repository) {
        this.repository = repository;
    }

    public void addRelationship(FKITGroup group, FKITSuperGroup superGroup) {
        FKITGroupToSuperGroupPK id = new FKITGroupToSuperGroupPK(superGroup, group);
        FKITGroupToSuperGroup relationship = new FKITGroupToSuperGroup(id);
        repository.save(relationship);
    }

    public List<FKITGroupToSuperGroup> getRelationships(FKITSuperGroup superGroup){
        return repository.findFKITGroupToSuperGroupsById_SuperGroup(superGroup);
    }
    public FKITSuperGroup getSuperGroup(FKITGroup group){
        System.out.println(repository.findFKITGroupToSuperGroupsById_Group(group));
        return repository.findFKITGroupToSuperGroupsById_Group(group).getId().getSuperGroup();
    }
    public void deleteRelationship(FKITGroup group, FKITSuperGroup superGroup){
        repository.delete(repository.findFKITGroupToSuperGroupsById_GroupAndId_SuperGroup(group, superGroup));
    }
}
