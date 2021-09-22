package it.chalmers.gamma.app.group;

import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.UserMembership;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    void create(Group group);
    void save(Group group);
    void delete(GroupId groupId) throws GroupNotFoundException;

    List<Group> getAll();
    List<Group> getAllBySuperGroup(SuperGroupId superGroupId);

    List<UserMembership> getGroupsByUser(UserId userId);

    Optional<Group> get(GroupId groupId);

    class GroupNotFoundException extends Exception { }

}
