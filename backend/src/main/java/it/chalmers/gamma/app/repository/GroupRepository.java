package it.chalmers.gamma.app.repository;

import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.domain.user.UserMembership;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    void save(Group group);
    void delete(GroupId groupId) throws GroupNotFoundException;

    List<Group> getAll();
    List<Group> getAllBySuperGroup(SuperGroupId superGroupId);
    List<Group> getAllByPost(PostId postId);

    List<UserMembership> getGroupsByUser(UserId userId);

    Optional<Group> get(GroupId groupId);

    class GroupNotFoundException extends Exception { }

}
