package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.group.GroupRepository;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GroupRepositoryAdapter implements GroupRepository {
    @Override
    public void create(Group group) {

    }

    @Override
    public void save(Group group) {

    }

    @Override
    public void delete(GroupId groupId) throws GroupNotFoundException {

    }

    @Override
    public List<Group> getAll() {
        return Collections.emptyList();
    }

    @Override
    public List<Group> getAllBySuperGroup(SuperGroupId superGroupId) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Group> get(GroupId groupId) {
        return Optional.empty();
    }
}
