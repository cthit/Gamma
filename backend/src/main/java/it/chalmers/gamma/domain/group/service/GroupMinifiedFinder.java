package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.data.dto.GroupMinifiedDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupMinifiedFinder implements GetEntity<GroupId, GroupMinifiedDTO>, GetAllEntities<GroupMinifiedDTO> {

    private final GroupFinder groupFinder;

    public GroupMinifiedFinder(GroupFinder groupFinder) {
        this.groupFinder = groupFinder;
    }

    public List<GroupMinifiedDTO> getAll() {
        return this.groupFinder.getAll().stream().map(GroupMinifiedDTO::new).collect(Collectors.toList());
    }

    public GroupMinifiedDTO get(GroupId id) throws EntityNotFoundException {
        return new GroupMinifiedDTO(this.groupFinder.get(id));
    }

}
