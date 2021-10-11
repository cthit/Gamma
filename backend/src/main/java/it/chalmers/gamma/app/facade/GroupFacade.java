package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.repository.GroupRepository;
import it.chalmers.gamma.app.repository.SuperGroupRepository;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.group.GroupMember;
import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.repository.PostRepository;
import it.chalmers.gamma.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupFacade extends Facade {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;

    public GroupFacade(AccessGuardUseCase accessGuard,
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
        Group group = new Group(
                GroupId.generate(),
                0,
                new Email(newGroup.email),
                new Name(newGroup.name),
                new PrettyName(newGroup.prettyName),
                this.superGroupRepository.get(new SuperGroupId(newGroup.superGroup)).orElseThrow(),
                new ArrayList<>(),
                Optional.empty(),
                Optional.empty()
        );
        this.groupRepository.save(group);
    }

    public record UpdateGroup(UUID id,
                              int version,
                              String name,
                              String prettyName,
                              UUID superGroup,
                              String email) {

    }

    public void updateGroup(UpdateGroup updateGroup) {
        GroupId groupId = new GroupId(updateGroup.id);
        Group oldGroup = this.groupRepository.get(groupId).orElseThrow();
        Group newGroup = oldGroup.with()
                .version(updateGroup.version)
                .name(new Name(updateGroup.name))
                .prettyName(new PrettyName(updateGroup.prettyName))
                .superGroup(this.superGroupRepository.get(new SuperGroupId(updateGroup.superGroup)).orElseThrow())
                .email(new Email(updateGroup.email))
                .build();

        this.groupRepository.save(newGroup);
    }

    public record ShallowMember(UUID userId, UUID postId, String unofficialPostName) {
    }

    public void setGroupMembers(UUID groupId, List<ShallowMember> newMembers) {
        Group oldGroup = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        this.groupRepository.save(oldGroup.withGroupMembers(
                newMembers.stream().map(shallowMember ->
                        new GroupMember(
                                this.postRepository.get(new PostId(shallowMember.postId)).orElseThrow(),
                                new UnofficialPostName(shallowMember.unofficialPostName),
                                this.userRepository.get(new UserId(shallowMember.userId)).orElseThrow()
                        )
                ).toList()
        ));
    }

    public void delete(UUID id) throws GroupRepository.GroupNotFoundException {
        this.groupRepository.delete(new GroupId(id));
    }

    public record GroupMemberDTO(UserFacade.UserDTO user, PostFacade.PostDTO post, String unofficialPostName) {
        public GroupMemberDTO(GroupMember groupMember) {
            this(new UserFacade.UserDTO(groupMember.user()), new PostFacade.PostDTO(groupMember.post()), groupMember.unofficialPostName().value());
        }
    }

    public record GroupDTO(UUID id, int version, String name, String email, String prettyName, List<GroupMemberDTO> groupMembers, SuperGroupFacade.SuperGroupDTO superGroup) {
        public GroupDTO(Group group) {
            this(group.id().value(),
                    group.version(),
                    group.name().value(),
                    group.email().value(),
                    group.prettyName().value(),
                    group.groupMembers().stream().map(GroupMemberDTO::new).toList(),
                    new SuperGroupFacade.SuperGroupDTO(group.superGroup())
            );
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

}
