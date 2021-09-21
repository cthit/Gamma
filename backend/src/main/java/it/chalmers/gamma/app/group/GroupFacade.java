package it.chalmers.gamma.app.group;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.app.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupFacade extends Facade {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public GroupFacade(AccessGuard accessGuard,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       PostRepository postRepository) {
        super(accessGuard);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public record NewMemberDTO(UserId userId, PostId postId, String unofficialPostName) { }

    public void setMembers(GroupId groupId, List<NewMemberDTO> newMembers) {
    }

    public record NewGroup(Email email, Name name, PrettyName prettyName, SuperGroupId superGroupId) { }

    public void createGroup(NewGroup newGroup) {

    }

    public record GroupDTO(String name) {
        public GroupDTO(Group group) {
            this(group.name().value());
        }
    }


    public Optional<GroupDTO> get(GroupId groupId) {
        accessGuard.requireSignedIn();
        return this.groupRepository.get(groupId).map(GroupDTO::new);
    }

    public List<GroupDTO> getAll() {
        return this.groupRepository.getAll().stream().map(GroupDTO::new).toList();
    }

    public List<Group> getGroupsBySuperGroup(SuperGroupId superGroupId) {
        return this.groupRepository.getAllBySuperGroup(superGroupId);
    }

}
