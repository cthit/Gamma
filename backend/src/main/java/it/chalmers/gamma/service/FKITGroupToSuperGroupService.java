package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.pk.FKITGroupToSuperGroupPK;
import it.chalmers.gamma.db.repository.FKITGroupToSuperGroupRepository;

import it.chalmers.gamma.views.FKITGroupView;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FKITGroupToSuperGroupService {
    private final FKITGroupToSuperGroupRepository repository;
    private final FKITSuperGroupService fkitSuperGroupService;

    public FKITGroupToSuperGroupService(FKITGroupToSuperGroupRepository repository,
                                        FKITSuperGroupService fkitSuperGroupService) {
        this.repository = repository;
        this.fkitSuperGroupService = fkitSuperGroupService;
    }

    public void addRelationship(FKITGroup group, FKITSuperGroup superGroup) {
        FKITGroupToSuperGroupPK id = new FKITGroupToSuperGroupPK(superGroup, group);
        FKITGroupToSuperGroup relationship = new FKITGroupToSuperGroup(id);
        this.repository.save(relationship);
    }

    public List<FKITGroupToSuperGroup> getRelationships(FKITSuperGroup superGroup) {
        return this.repository.findFKITGroupToSuperGroupsById_SuperGroup(superGroup);
    }

    public List<FKITSuperGroup> getSuperGroups(FKITGroup group) {
        List<FKITGroupToSuperGroup> fkitGroupToSuperGroups =
                this.repository.findAllFKITGroupToSuperGroupsById_Group(group);
        return fkitGroupToSuperGroups.stream().map(fkitGroupToSuperGroup -> fkitGroupToSuperGroup.getId()
                .getSuperGroup()).collect(Collectors.toList());
    }

    public void deleteRelationship(FKITGroup group, FKITSuperGroup superGroup) {
        this.repository.delete(this.repository.findFKITGroupToSuperGroupsById_GroupAndId_SuperGroup(group, superGroup));
    }
    public List<FKITGroupToSuperGroup> getAllRelationships() {
        return this.repository.findAll();
    }

    /**
     * returns the subgroup that is currently the active group.
     *
     * @param superGroup the super group to retrieve active group from
     * @return the fkit group that is active
     */
    public List<FKITGroup> getActiveGroups(FKITSuperGroup superGroup) {
        List<FKITGroupToSuperGroup> relationships = this.repository
                .findFKITGroupToSuperGroupsById_SuperGroup(superGroup);
        List<FKITGroup> groups = new ArrayList<>();
        relationships.stream().filter(e -> e.getId().getGroup().isActive())
                .forEach(e -> groups.add(e.getId().getGroup()));
        return groups;
    }

    public List<FKITGroupView> getAllActiveGroups() {
        return this.fkitSuperGroupService.getAllGroups().stream()
                .map(g -> new FKITGroupView(g, this.getActiveGroups(g)))
                .filter(g -> !g.getGroups().isEmpty()).collect(Collectors.toList());
    }
}
