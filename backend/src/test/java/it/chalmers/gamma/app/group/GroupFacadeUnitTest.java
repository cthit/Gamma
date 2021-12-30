package it.chalmers.gamma.app.group;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.DomainUtils.*;
import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isClientApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class GroupFacadeUnitTest {

    @Mock
    private AccessGuard accessGuard;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private SuperGroupRepository superGroupRepository;
    @Mock
    private SettingsRepository settingsRepository;

    @InjectMocks
    private GroupFacade groupFacade;

    @Test
    public void Given_ValidNewGroup_Expect_create_To_Work() throws GroupFacade.SuperGroupNotFoundRuntimeException, GroupFacade.GroupAlreadyExistsException, GroupRepository.GroupAlreadyExistsException {
        UUID superGroupId = UUID.randomUUID();
        SuperGroup superGroup = new SuperGroup(
                new SuperGroupId(superGroupId),
                0,
                new Name("mysupergroup"),
                new PrettyName("My Super Group"),
                new SuperGroupType("cool"),
                new Text(
                        "Det här är coolt",
                        "This is a cool"
                )
        );

        given(superGroupRepository.get(new SuperGroupId(superGroupId)))
                .willReturn(Optional.of(superGroup));

        GroupFacade.NewGroup newGroup = new GroupFacade.NewGroup(
                "mygroup",
                "My Group",
                superGroupId
        );

        groupFacade.create(newGroup);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(captor.capture());
        Group capturedNewGroup = captor.getValue();

        Assertions.assertThat(capturedNewGroup.id())
                .isNotNull();

        Group expectedGroup = new Group(
                capturedNewGroup.id(),
                0,
                new Name(newGroup.name()),
                new PrettyName(newGroup.prettyName()),
                superGroup,
                Collections.emptyList(),
                Optional.empty(),
                Optional.empty()
        );

        Assertions.assertThat(capturedNewGroup)
                .isEqualTo(expectedGroup);

        InOrder inOrder = inOrder(accessGuard, groupRepository, superGroupRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(superGroupRepository).get(new SuperGroupId(superGroupId));
        inOrder.verify(groupRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_GroupWithNameThatAlreadyExists_Expect_create_To_Throw() throws GroupRepository.GroupAlreadyExistsException {
        doThrow(GroupRepository.GroupAlreadyExistsException.class)
                .when(this.groupRepository).save(any());

        UUID superGroupId = UUID.randomUUID();
        SuperGroup superGroup = new SuperGroup(
                new SuperGroupId(superGroupId),
                0,
                new Name("mysupergroup"),
                new PrettyName("My Super Group"),
                new SuperGroupType("cool"),
                new Text(
                        "Det här är coolt",
                        "This is a cool"
                )
        );

        given(superGroupRepository.get(new SuperGroupId(superGroupId)))
                .willReturn(Optional.of(superGroup));

        GroupFacade.NewGroup newGroup = new GroupFacade.NewGroup(
                "mygroup",
                "My Group",
                superGroupId
        );

        assertThatExceptionOfType(GroupFacade.GroupAlreadyExistsException.class)
                .isThrownBy(() -> groupFacade.create(newGroup));

    }

    @Test
    public void Given_GroupWithInvalidSuperGroup_Expect_create_To_Throw() {
        given(superGroupRepository.get(any()))
                .willReturn(Optional.empty());

        GroupFacade.NewGroup newGroup = new GroupFacade.NewGroup(
                "mygroup",
                "My Group",
                UUID.randomUUID()
        );

        assertThatExceptionOfType(GroupFacade.SuperGroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.create(newGroup));
    }

    @Test
    public void Given_Group_Expect_update_To_Work() throws GroupFacade.SuperGroupNotFoundRuntimeException, GroupFacade.GroupNotFoundRuntimeException, GroupFacade.GroupAlreadyExistsException, GroupRepository.GroupAlreadyExistsException {
        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));

        given(superGroupRepository.get(didit.id()))
                .willReturn(Optional.of(didit));

        GroupFacade.UpdateGroup updateGroup = new GroupFacade.UpdateGroup(
                digit19.id().value(),
                0,
                "digit182",
                "digIT 18",
                didit.id().value()
        );

        groupFacade.update(updateGroup);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(captor.capture());
        Group capturedNewGroup = captor.getValue();

        assertThat(capturedNewGroup)
                .isEqualTo(new Group(
                        digit19.id(),
                        0,
                        new Name("digit182"),
                        new PrettyName("digIT 18"),
                        didit,
                        digit19.groupMembers(),
                        digit19.avatarUri(),
                        digit19.bannerUri()
                ));

        InOrder inOrder = inOrder(accessGuard, groupRepository, superGroupRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(superGroupRepository).get(didit.id());
        inOrder.verify(groupRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_InvalidUpdateGroup_Expect_update_To_Throw_GroupNotFound() {
        given(groupRepository.get(any()))
                .willReturn(Optional.empty());

        GroupFacade.UpdateGroup updateGroup = new GroupFacade.UpdateGroup(
                digit19.id().value(),
                0,
                "digit182",
                "digIT 18",
                didit.id().value()
        );

        assertThatExceptionOfType(GroupFacade.GroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.update(updateGroup));
    }

    @Test
    public void Given_InvalidUpdateGroup_Expect_update_To_Throw_SuperGroupNotFound() {
        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));

        given(superGroupRepository.get(didit.id()))
                .willReturn(Optional.empty());

        GroupFacade.UpdateGroup updateGroup = new GroupFacade.UpdateGroup(
                digit19.id().value(),
                0,
                "digit182",
                "digIT 18",
                didit.id().value()
        );

        assertThatExceptionOfType(GroupFacade.SuperGroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.update(updateGroup));
    }

    @Test
    public void Given_ValidGroup_Expect_setMembers_To_Work() throws GroupFacade.GroupNotFoundRuntimeException, GroupFacade.UserNotFoundRuntimeException, GroupFacade.PostNotFoundRuntimeException, GroupRepository.GroupAlreadyExistsException {
        given(userRepository.get(u8.id()))
                .willReturn(Optional.of(u8));
        given(userRepository.get(u9.id()))
                .willReturn(Optional.of(u9));
        given(userRepository.get(u10.id()))
                .willReturn(Optional.of(u10));
        given(userRepository.get(u11.id()))
                .willReturn(Optional.of(u11));

        given(postRepository.get(chair.id()))
                .willReturn(Optional.of(chair));
        given(postRepository.get(treasurer.id()))
                .willReturn(Optional.of(treasurer));
        given(postRepository.get(member.id()))
                .willReturn(Optional.of(member));

        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));

        given(superGroupRepository.get(didit.id()))
                .willReturn(Optional.of(didit));

        List<GroupFacade.ShallowMember> newMembers = List.of(
                new GroupFacade.ShallowMember(u8.id().value(), chair.id().value(), "root"),
                new GroupFacade.ShallowMember(u9.id().value(), member.id().value(), "ServerChef"),
                new GroupFacade.ShallowMember(u10.id().value(), member.id().value(), null),
                new GroupFacade.ShallowMember(u11.id().value(), treasurer.id().value(), "")
        );

        groupFacade.setMembers(digit19.id().value(), newMembers);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(captor.capture());
        Group capturedNewGroup = captor.getValue();

        assertThat(capturedNewGroup)
                .isEqualTo(digit19.withGroupMembers(List.of(
                        gm(u8, chair, new UnofficialPostName("root")),
                        gm(u9, member, new UnofficialPostName("ServerChef")),
                        gm(u10, member),
                        gm(u11, treasurer)
                )));

        InOrder inOrder = inOrder(accessGuard, groupRepository, superGroupRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(groupRepository).save(any());
    }

    @Test
    public void Given_InvalidGroup_Expect_setMembers_To_Throw() {
        given(groupRepository.get(any()))
                .willReturn(Optional.empty());

        List<GroupFacade.ShallowMember> newMembers = List.of(
                new GroupFacade.ShallowMember(u8.id().value(), chair.id().value(), "root")
        );

        assertThatExceptionOfType(GroupFacade.GroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.setMembers(digit19.id().value(), newMembers));
    }

    @Test
    public void Given_InvalidUser_Expect_setMembers_To_Throw() {
        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));
        given(postRepository.get(chair.id()))
                .willReturn(Optional.of(chair));
        given(userRepository.get((UserId) any()))
                .willReturn(Optional.empty());

        List<GroupFacade.ShallowMember> newMembers = List.of(
                new GroupFacade.ShallowMember(u8.id().value(), chair.id().value(), "root")
        );

        assertThatExceptionOfType(GroupFacade.UserNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.setMembers(digit19.id().value(), newMembers));
    }

    @Test
    public void Given_InvalidPosts_Expect_setMembers_To_Throw() {
        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));
        given(postRepository.get(chair.id()))
                .willReturn(Optional.empty());
        given(userRepository.get(u8.id()))
                .willReturn(Optional.of(u8));

        List<GroupFacade.ShallowMember> newMembers = List.of(
                new GroupFacade.ShallowMember(u8.id().value(), chair.id().value(), "root")
        );

        assertThatExceptionOfType(GroupFacade.PostNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.setMembers(digit19.id().value(), newMembers));
    }

    @Test
    public void Given_ValidGroup_Expect_delete_To_Work() throws GroupFacade.GroupNotFoundRuntimeException, GroupRepository.GroupNotFoundException {
        GroupId groupId = GroupId.generate();

        groupFacade.delete(groupId.value());

        ArgumentCaptor<GroupId> captor = ArgumentCaptor.forClass(GroupId.class);
        verify(groupRepository).delete(captor.capture());
        GroupId deleteGroupId = captor.getValue();

        Assertions.assertThat(deleteGroupId)
                .isEqualTo(groupId);

        InOrder inOrder = inOrder(accessGuard, groupRepository, superGroupRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(groupRepository).delete(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_InvalidGroup_Expect_delete_To_Throw() throws GroupRepository.GroupNotFoundException {
        doThrow(GroupRepository.GroupNotFoundException.class)
                .when(groupRepository).delete(any());

        assertThatExceptionOfType(GroupFacade.GroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupFacade.delete(UUID.randomUUID()));
    }

    @Test
    public void Given_ValidGroupId_Expect_getWithMembers_To_Work() {
        given(groupRepository.get(digit19.id()))
                .willReturn(Optional.of(digit19));

        GroupFacade.GroupWithMembersDTO group = this.groupFacade.getWithMembers(digit19.id().value())
                .orElseThrow();

        assertThat(group)
                .isEqualTo(
                        new GroupFacade.GroupWithMembersDTO(
                                digit19.id().value(),
                                0,
                                digit19.name().value(),
                                digit19.prettyName().value(),
                                digit19.groupMembers().stream().map(GroupFacade.GroupMemberDTO::new).toList(),
                                new SuperGroupFacade.SuperGroupDTO(digit)
                        )
                );

        InOrder inOrder = inOrder(accessGuard, groupRepository);

        inOrder.verify(accessGuard).require(isSignedIn());
        inOrder.verify(groupRepository).get(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_Groups_Expect_getAll_To_Work() {
        given(groupRepository.getAll())
                .willReturn(List.of(digit18, digit19, prit18, prit19));

        List<GroupFacade.GroupDTO> groups = groupFacade.getAll();

        assertThat(groups)
                .containsExactlyInAnyOrder(
                        new GroupFacade.GroupDTO(digit18),
                        new GroupFacade.GroupDTO(digit19),
                        new GroupFacade.GroupDTO(prit18),
                        new GroupFacade.GroupDTO(prit19)
                );

        InOrder inOrder = inOrder(accessGuard, groupRepository);

        inOrder.verify(accessGuard).requireEither(isSignedIn(), isClientApi());
        inOrder.verify(groupRepository).getAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_ValidGroups_Expect_getAllForInfoApi() {
        given(settingsRepository.getSettings())
                .willReturn(new Settings(
                        Instant.now(),
                        List.of(committee, board)
                ));

        given(groupRepository.getAll())
                .willReturn(List.of(digit18, digit19, prit18, prit19, styrit18, styrit19, drawit18, drawit19));

        List<GroupFacade.GroupWithMembersDTO> groups = groupFacade.getAllForInfoApi();


        assertThat(groups)
                .containsExactlyInAnyOrder(
                        new GroupFacade.GroupWithMembersDTO(digit19),
                        new GroupFacade.GroupWithMembersDTO(prit19),
                        new GroupFacade.GroupWithMembersDTO(styrit19)
                );

        InOrder inOrder = inOrder(accessGuard, groupRepository);

        //TODO: Make sure only info can call
        inOrder.verify(accessGuard).require(isApi(any()));
        inOrder.verify(groupRepository).getAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_ValidGroups_Expect_getAllBySuperGroup_To_Work() {
        given(groupRepository.getAllBySuperGroup(didit.id()))
                .willReturn(List.of(digit18));

        List<GroupFacade.GroupWithMembersDTO> groups = groupFacade.getAllBySuperGroup(didit.id().value());

        assertThat(groups)
                .containsExactlyInAnyOrder(
                        new GroupFacade.GroupWithMembersDTO(digit18)
                );

        InOrder inOrder = inOrder(accessGuard, groupRepository);

        inOrder.verify(accessGuard).require(isSignedIn());
        inOrder.verify(groupRepository).getAllBySuperGroup(any());
        inOrder.verifyNoMoreInteractions();
    }

}
