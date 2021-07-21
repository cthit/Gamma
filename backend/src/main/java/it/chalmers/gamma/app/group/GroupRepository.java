package it.chalmers.gamma.app.group;

import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.SuperGroupId;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    void create(Group group);
    void save(Group group);
    void delete(GroupId groupId) throws GroupNotFoundException;

    List<Group> getAll(Group group);
    List<Group> getAllBySuperGroup(SuperGroupId superGroupId);

    Optional<Group> get(GroupId groupId);

    class GroupNotFoundException extends Exception { }

}
