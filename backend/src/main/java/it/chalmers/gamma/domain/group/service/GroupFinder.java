package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.EntityExists;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
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
                .map(Group::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList()
                );
    }

    public GroupDTO get(GroupId id) throws EntityNotFoundException {
        return fromShallow(getGroupEntity(id).toDTO());
    }

    public GroupDTO getByName(String name) throws EntityNotFoundException {
        return fromShallow(getGroupEntityByName(name).toDTO());
    }

    protected Group getGroupEntity(GroupId id) throws EntityNotFoundException {
        return this.groupRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected Group getGroupEntity(GroupShallowDTO group) throws EntityNotFoundException {
        return getGroupEntity(group.getId());
    }

    protected Group getGroupEntity(GroupDTO group) throws EntityNotFoundException {
        return getGroupEntity(group.getId());
    }

    protected Group getGroupEntityByName(String name) throws EntityNotFoundException {
        return this.groupRepository.findByName(name)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<GroupDTO> getGroupsBySuperGroup(SuperGroupId superGroupId) throws EntityNotFoundException {
        if(superGroupFinder.exists(superGroupId)) {
            throw new EntityNotFoundException();
        }

        return this.groupRepository.findAllBySuperGroupId(superGroupId)
                .stream()
                .map(Group::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinifiedBySuperGroup(SuperGroupId superGroupId) throws EntityNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getActiveGroupsMinifiedBySuperGroup(SuperGroupId superGroupId) throws EntityNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .filter(GroupBaseDTO::isActive)
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    protected GroupDTO fromShallow(GroupShallowDTO group) {
        try {
            return new GroupDTO.GroupDTOBuilder()
                    .id(group.getId())
                    .becomesActive(group.getBecomesActive())
                    .becomesInactive(group.getBecomesInactive())
                    .email(group.getEmail())
                    .name(group.getName())
                    .prettyName(group.getPrettyName())
                    .avatarUrl(group.getAvatarURL())
                    .superGroup(superGroupFinder.get(group.getSuperGroupId()))
                    .build();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
