package it.chalmers.gamma.app.group;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isClientApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

@Service
public class GroupFacade extends Facade {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;
    private final SettingsRepository settingsRepository;

    public GroupFacade(AccessGuard accessGuard,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       SuperGroupRepository superGroupRepository, SettingsRepository settingsRepository) {
        super(accessGuard);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
        this.settingsRepository = settingsRepository;
    }

    public record NewGroup(String name,
                           String prettyName,
                           UUID superGroup,
                           String email) { }

    public void createGroup(NewGroup newGroup) {
        accessGuard.require(isAdmin());

        Group group = new Group(
                GroupId.generate(),
                0,
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
        accessGuard.require(isAdmin());

        GroupId groupId = new GroupId(updateGroup.id);
        Group oldGroup = this.groupRepository.get(groupId).orElseThrow();
        Group newGroup = oldGroup.with()
                .version(updateGroup.version)
                .name(new Name(updateGroup.name))
                .prettyName(new PrettyName(updateGroup.prettyName))
                .superGroup(this.superGroupRepository.get(new SuperGroupId(updateGroup.superGroup)).orElseThrow())
                .build();

        this.groupRepository.save(newGroup);
    }

    public record ShallowMember(UUID userId, UUID postId, String unofficialPostName) {
    }

    public void setGroupMembers(UUID groupId, List<ShallowMember> newMembers) {
        accessGuard.require(isAdmin());

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
        accessGuard.require(isAdmin());

        this.groupRepository.delete(new GroupId(id));
    }

    public record GroupMemberDTO(UserFacade.UserDTO user, PostFacade.PostDTO post, String unofficialPostName) {
        public GroupMemberDTO(GroupMember groupMember) {
            this(new UserFacade.UserDTO(groupMember.user()), new PostFacade.PostDTO(groupMember.post()), groupMember.unofficialPostName().value());
        }
    }

    public record GroupDTO(UUID id, String name, String prettyName, SuperGroupFacade.SuperGroupDTO superGroup) {
        public GroupDTO(Group group) {
            this(group.id().value(),
                    group.name().value(),
                    group.prettyName().value(),
                    new SuperGroupFacade.SuperGroupDTO(group.superGroup())
            );
        }
    }

    public record GroupWithMembersDTO(UUID id, int version, String name, String prettyName, List<GroupMemberDTO> groupMembers, SuperGroupFacade.SuperGroupDTO superGroup) {
        public GroupWithMembersDTO(Group group) {
            this(group.id().value(),
                    group.version(),
                    group.name().value(),
                    group.prettyName().value(),
                    group.groupMembers().stream().map(GroupMemberDTO::new).toList(),
                    new SuperGroupFacade.SuperGroupDTO(group.superGroup())
            );
        }
    }

    public Optional<GroupWithMembersDTO> getWithMembers(UUID groupId) {
        accessGuard.require(isSignedIn());

        return this.groupRepository.get(new GroupId(groupId)).map(GroupWithMembersDTO::new);
    }

    public List<GroupDTO> getAll() {
        accessGuard.require(isClientApi());

        return this.groupRepository.getAll().stream().map(GroupDTO::new).toList();
    }

    public List<GroupWithMembersDTO> getAllWithMembers() {
        accessGuard.require(isSignedIn());

        return this.groupRepository.getAll().stream().map(GroupWithMembersDTO::new).toList();
    }

    public List<GroupWithMembersDTO> getAllForInfo() {
        accessGuard.require(isApi(ApiKeyType.INFO));

        List<SuperGroupType> allowedSuperGroupType = this.settingsRepository.getSettings().infoSuperGroupTypes();

        return this.groupRepository.getAll()
                .stream()
                .filter(group -> allowedSuperGroupType.contains(group.superGroup().type()))
                .map(GroupWithMembersDTO::new)
                .toList();
    }

    public List<GroupWithMembersDTO> getGroupsBySuperGroup(UUID superGroupId) {
        accessGuard.require(isSignedIn());

        return this.groupRepository.getAllBySuperGroup(new SuperGroupId(superGroupId))
                .stream()
                .map(GroupWithMembersDTO::new)
                .toList();
    }

}
