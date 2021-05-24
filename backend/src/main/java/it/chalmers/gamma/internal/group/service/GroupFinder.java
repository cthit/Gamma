package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.util.domain.abstraction.EntityExists;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupFinder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupFinder implements GetEntity<GroupId, GroupDTO>, GetAllEntities<GroupDTO>, EntityExists<GroupId> {

    private final GroupRepository groupRepository;
    private final SuperGroupFinder superGroupFinder;

    public GroupFinder(GroupRepository groupRepository,
                       SuperGroupFinder superGroupFinder) {
        this.groupRepository = groupRepository;
        this.superGroupFinder = superGroupFinder;
    }

    public boolean exists(GroupId groupId) {
        return this.groupRepository.existsById(groupId);
    }

    public List<GroupDTO> getAll() {
        return this.groupRepository
                .findAll()
                .stream()
                .map(GroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public GroupDTO get(GroupId id) throws EntityNotFoundException {
        return fromShallow(getGroupEntity(id).toDTO());
    }

    public GroupDTO getByName(Name name) throws EntityNotFoundException {
        return fromShallow(getGroupEntityByName(name).toDTO());
    }

    protected GroupEntity getGroupEntity(GroupId id) throws EntityNotFoundException {
        return this.groupRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected GroupEntity getGroupEntity(GroupShallowDTO group) throws EntityNotFoundException {
        return getGroupEntity(group.id());
    }

    protected GroupEntity getGroupEntity(GroupDTO group) throws EntityNotFoundException {
        return getGroupEntity(group.id());
    }

    protected GroupEntity getGroupEntityByName(Name name) throws EntityNotFoundException {
        return this.groupRepository.findByName(name)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<GroupDTO> getGroupsBySuperGroup(SuperGroupId superGroupId) throws EntityNotFoundException {
        if(superGroupFinder.exists(superGroupId)) {
            throw new EntityNotFoundException();
        }

        return this.groupRepository.findAllBySuperGroupId(superGroupId)
                .stream()
                .map(GroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinifiedBySuperGroup(SuperGroupId superGroupId) throws EntityNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    protected GroupDTO fromShallow(GroupShallowDTO group) {
        try {
            return new GroupDTO(
                    group.id(),
                    group.email(),
                    group.name(),
                    group.prettyName(),
                    superGroupFinder.get(group.superGroupId())
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
