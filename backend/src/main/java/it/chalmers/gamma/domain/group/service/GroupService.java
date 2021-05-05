package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GroupService implements CreateEntity<GroupShallowDTO>, DeleteEntity<GroupId>, UpdateEntity<GroupShallowDTO> {

    private final GroupRepository groupRepository;
    private final GroupFinder groupFinder;

    public GroupService(GroupRepository groupRepository,
                        GroupFinder groupFinder) {
        this.groupRepository = groupRepository;
        this.groupFinder = groupFinder;
    }

    @Override
    public void create(GroupShallowDTO group) throws EntityAlreadyExistsException {
        this.groupRepository.save(new Group(group));
    }

    @Override
    public void delete(GroupId id) throws EntityNotFoundException {
        this.groupRepository.deleteById(id);
    }

    @Override
    public void update(GroupShallowDTO newEdit) throws EntityNotFoundException {
        Group group = this.groupFinder.getGroupEntity(newEdit.id());
        group.apply(newEdit);
        this.groupRepository.save(group);
    }
}
