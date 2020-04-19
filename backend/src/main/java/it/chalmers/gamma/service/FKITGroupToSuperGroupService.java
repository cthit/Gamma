package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.pk.FKITGroupToSuperGroupPK;
import it.chalmers.gamma.db.repository.FKITGroupToSuperGroupRepository;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupToSuperGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FKITGroupToSuperGroupService {
    private final FKITGroupToSuperGroupRepository repository;
    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITGroupService fkitGroupService;

    public FKITGroupToSuperGroupService(FKITGroupToSuperGroupRepository repository,
                                        FKITSuperGroupService fkitSuperGroupService,
                                        FKITGroupService fkitGroupService) {
        this.repository = repository;
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupService = fkitGroupService;
    }

    public void addRelationship(FKITGroupDTO groupDTO, FKITSuperGroupDTO superGroupDTO) {
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(superGroupDTO);
        FKITGroup group = this.fkitGroupService.getGroup(groupDTO);
        FKITGroupToSuperGroupPK id = new FKITGroupToSuperGroupPK(superGroup, group);
        FKITGroupToSuperGroup relationship = new FKITGroupToSuperGroup(id);
        this.repository.save(relationship);
    }

    public List<FKITGroupToSuperGroupDTO> removeOldGroups(List<FKITGroupToSuperGroupDTO> groupToSuperGroupDTOS) {
        List<FKITGroupToSuperGroupDTO> relationships = new ArrayList<>(groupToSuperGroupDTOS);
        for (FKITGroupToSuperGroupDTO relationship : relationships) {
            FKITSuperGroupDTO superGroupDTO = relationship.getSuperGroup();
            if (superGroupDTO.getType() == GroupType.ALUMNI) {
                relationships = relationships.stream().filter(r -> !(r.getGroup().equals(relationship.getGroup())
                        && r.getSuperGroup().getType() != GroupType.ALUMNI)).collect(Collectors.toList());
            }
        }
        return relationships;
    }

    public List<FKITGroupToSuperGroupDTO> getRelationships(List<FKITGroupDTO> groupDTOS) {
        return groupDTOS.stream().map(this::getRelationships).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<FKITGroupToSuperGroupDTO> getRelationships(FKITSuperGroupDTO superGroup) {
        return this.repository.findFKITGroupToSuperGroupsById_SuperGroup_Id(superGroup.getId())
                .stream().map(FKITGroupToSuperGroup::toDTO).collect(Collectors.toList());
    }

    public List<FKITGroupToSuperGroupDTO> getRelationships(FKITGroupDTO groupDTO) {
        FKITGroup group = this.fkitGroupService.getGroup(groupDTO);
        return this.repository.findAllFKITGroupToSuperGroupsById_Group(group)
                .stream().map(FKITGroupToSuperGroup::toDTO).collect(Collectors.toList());
    }

    public List<FKITSuperGroupDTO> getSuperGroups(FKITGroupDTO groupDTO) {
        FKITGroup group = this.fkitGroupService.getGroup(groupDTO);
        List<FKITGroupToSuperGroup> fkitGroupToSuperGroups =
                this.repository.findAllFKITGroupToSuperGroupsById_Group(group);
        return fkitGroupToSuperGroups.stream().map(fkitGroupToSuperGroup -> fkitGroupToSuperGroup.getId()
                .getSuperGroup().toDTO()).collect(Collectors.toList());
    }

    public void deleteRelationship(FKITGroupDTO groupDTO, FKITSuperGroupDTO superGroupDTO) {
        FKITGroup group = this.fkitGroupService.getGroup(groupDTO);
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(superGroupDTO);
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
    public List<FKITGroupDTO> getActiveGroups(FKITSuperGroupDTO superGroup) {
        List<FKITGroupToSuperGroup> relationships = this.repository
                .findFKITGroupToSuperGroupsById_SuperGroup_Id(superGroup.getId());
        List<FKITGroupDTO> groups = new ArrayList<>();
        relationships.stream().filter(e -> e.getId().getGroup().toDTO().isActive())
                .forEach(e -> groups.add(e.getId().getGroup().toDTO()));
        return groups;
    }

    public List<FKITGroupDTO> getAllActiveGroups() {
        return this.fkitGroupService.getGroups().stream().filter(FKITGroupDTO::isActive).collect(Collectors.toList());
    }
}
