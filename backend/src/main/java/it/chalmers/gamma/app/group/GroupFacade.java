package it.chalmers.gamma.app.group;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupBuilder;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.GroupMember;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.app.user.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupFacade extends Facade {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;

    public GroupFacade(AccessGuard accessGuard,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       SuperGroupRepository superGroupRepository) {
        super(accessGuard);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
    }

    public record NewGroup(String name,
                           String prettyName,
                           UUID superGroup,
                           String email) { }

    public void createGroup(NewGroup newGroup) {
        throw new UnsupportedOperationException();
    }

    public record UpdateGroup(UUID id,
                              String name,
                              String prettyName,
                              UUID superGroup,
                              String email) {

    }
    @Transactional
    public void updateGroup(UpdateGroup updateGroup) {
        GroupId groupId = new GroupId(updateGroup.id);
        Group oldGroup = this.groupRepository.get(groupId).orElseThrow();
        Group newGroup = oldGroup.with()
                .name(new Name(updateGroup.name))
                .prettyName(new PrettyName(updateGroup.prettyName))
                .superGroup(this.superGroupRepository.get(new SuperGroupId(updateGroup.superGroup)).orElseThrow())
                .email(new Email(updateGroup.email))
                .build();

        this.groupRepository.save(newGroup);
    }

    public void delete(UUID id) throws GroupRepository.GroupNotFoundException {
        this.groupRepository.delete(new GroupId(id));
    }

    public record GroupDTO(UUID id, String name, List<GroupMember> groupMembers) {

        public GroupDTO(Group group) {
            this(group.id().value(), group.name().value(), group.groupMembers());
        }
    }
    public Optional<GroupDTO> get(UUID groupId) {
        accessGuard.requireSignedIn();
        return this.groupRepository.get(new GroupId(groupId)).map(GroupDTO::new);
    }

    public List<GroupDTO> getAll() {
        return this.groupRepository.getAll().stream().map(GroupDTO::new).toList();
    }

    public List<GroupDTO> getGroupsBySuperGroup(UUID superGroupId) {
        return this.groupRepository.getAllBySuperGroup(new SuperGroupId(superGroupId))
                .stream()
                .map(GroupDTO::new)
                .toList();
    }

    public void addMember(UUID groupId, UUID userId, UUID postId, String unofficialPostName) {
        throw new UnsupportedOperationException();
    }

    public void removeMember(UUID groupId, UUID userId, UUID postId) {
        throw new UnsupportedOperationException();
    }

    public record UpdateMember(String unofficialPostName) { }

    public void updateMember(UUID groupId, UUID userId, UUID postId, UpdateMember updateMember) {
        throw new UnsupportedOperationException();
    }

}
