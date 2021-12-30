package it.chalmers.gamma.app.group.domain;

import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    void save(Group group)
            throws GroupAlreadyExistsException, SuperGroupNotFoundRuntimeException,
            UserNotFoundRuntimeException, PostNotFoundRuntimeException;
    void delete(GroupId groupId) throws GroupNotFoundException;

    List<Group> getAll();
    List<Group> getAllBySuperGroup(SuperGroupId superGroupId);
    List<Group> getAllByPost(PostId postId);

    List<UserMembership> getAllByUser(UserId userId);

    Optional<Group> get(GroupId groupId);

    class GroupNotFoundException extends Exception { }
    class GroupAlreadyExistsException extends Exception { }

    class SuperGroupNotFoundRuntimeException extends RuntimeException { }
    class UserNotFoundRuntimeException extends RuntimeException { }
    class PostNotFoundRuntimeException extends RuntimeException { }

}
