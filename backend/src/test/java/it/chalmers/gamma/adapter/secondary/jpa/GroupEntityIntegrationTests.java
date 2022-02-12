package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.DomainUtils;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.UserMembership;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static it.chalmers.gamma.DomainUtils.addAll;
import static it.chalmers.gamma.DomainUtils.asSaved;
import static it.chalmers.gamma.DomainUtils.chair;
import static it.chalmers.gamma.DomainUtils.defaultSettings;
import static it.chalmers.gamma.DomainUtils.didit;
import static it.chalmers.gamma.DomainUtils.digit;
import static it.chalmers.gamma.DomainUtils.digit17;
import static it.chalmers.gamma.DomainUtils.digit18;
import static it.chalmers.gamma.DomainUtils.digit19;
import static it.chalmers.gamma.DomainUtils.drawit;
import static it.chalmers.gamma.DomainUtils.drawit18;
import static it.chalmers.gamma.DomainUtils.drawit19;
import static it.chalmers.gamma.DomainUtils.gm;
import static it.chalmers.gamma.DomainUtils.member;
import static it.chalmers.gamma.DomainUtils.prit18;
import static it.chalmers.gamma.DomainUtils.prit19;
import static it.chalmers.gamma.DomainUtils.removeLockedUsers;
import static it.chalmers.gamma.DomainUtils.styrit18;
import static it.chalmers.gamma.DomainUtils.styrit19;
import static it.chalmers.gamma.DomainUtils.treasurer;
import static it.chalmers.gamma.DomainUtils.u0;
import static it.chalmers.gamma.DomainUtils.u1;
import static it.chalmers.gamma.DomainUtils.u3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GroupRepositoryAdapter.class,
        GroupEntityConverter.class,
        SuperGroupEntityConverter.class,
        UserEntityConverter.class,
        PostEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserAccessGuard.class,
        PostRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupTypeRepositoryAdapter.class,
        SettingsRepositoryAdapter.class})
public class GroupEntityIntegrationTests {

    @Autowired
    private GroupRepositoryAdapter groupRepositoryAdapter;
    @Autowired
    private SuperGroupRepository superGroupRepository;
    @Autowired
    private SuperGroupTypeRepository superGroupTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_ValidGroup_Expect_save_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        Group groupToSave = digit18;
        addGroup(groupToSave);

        Group savedGroup = groupRepositoryAdapter.get(groupToSave.id())
                .orElseThrow();

        assertThat(savedGroup)
                .isEqualTo(asSaved(removeLockedUsers(groupToSave)));
    }

    @Test
    public void Given_SameGroupIdTwice_Expect_save_To_Throw() {
        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> addGroup(digit18, digit18));

        assertThat(groupRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        asSaved(removeLockedUsers(digit18))
                );
    }

    @Test
    public void Given_SameGroupNameTwice_Expect_save_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(digit18);
        assertThatExceptionOfType(GroupRepository.GroupNameAlreadyExistsException.class)
                .isThrownBy(() -> this.groupRepositoryAdapter.save(digit18.withId(GroupId.generate())));
    }

    @Test
    public void Given_GroupWithInvalidSuperGroup_Expect_save_To_Throw() {
        Group group = digit18;
        addAll(userRepository, group.groupMembers().stream().map(GroupMember::user).toList());
        addAll(postRepository, group.groupMembers().stream().map(GroupMember::post).distinct().toList());

        assertThatExceptionOfType(GroupRepository.SuperGroupNotFoundRuntimeException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(group));
    }

    @Test
    public void Given_GroupWithInvalidUser_Expect_save_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        Group group = digit18;
        superGroupTypeRepository.add(group.superGroup().type());
        superGroupRepository.save(group.superGroup());
        addAll(postRepository, group.groupMembers().stream().map(GroupMember::post).distinct().toList());

        assertThatExceptionOfType(GroupRepository.UserNotFoundRuntimeException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(group));
    }

    @Test
    public void Given_GroupWithInvalidPost_Expect_save_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        Group group = digit18;
        superGroupTypeRepository.add(group.superGroup().type());
        superGroupRepository.save(group.superGroup());
        addAll(userRepository, group.groupMembers().stream().map(GroupMember::user).toList());

        assertThatExceptionOfType(GroupRepository.PostNotFoundRuntimeException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(group));
    }

    @Test
    public void Given_ValidGroup_Expect_delete_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        Group group = digit18;

        addGroup(group);
        assertThatNoException()
                .isThrownBy(() -> groupRepositoryAdapter.delete(group.id()));

        assertThat(groupRepositoryAdapter.get(group.id()))
                .isEmpty();
    }

    @Test
    public void Given_GroupWithInvalidVersion_Expect_save_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        superGroupTypeRepository.add(digit.type());
        superGroupRepository.save(digit);

        Group group = digit18;

        //Default version is 0
        addGroup(group);
        group = groupRepositoryAdapter.get(group.id()).orElseThrow();

        //Version 1
        groupRepositoryAdapter.save(group.withPrettyName(new PrettyName("what")));
        group = groupRepositoryAdapter.get(group.id()).orElseThrow();

        //Version 2
        groupRepositoryAdapter.save(group.withGroupMembers(List.of(gm(u1, chair))));
        group = groupRepositoryAdapter.get(group.id()).orElseThrow();

        //Version 3
        groupRepositoryAdapter.save(group.withAvatarUri(Optional.of(new ImageUri("myimage.png"))));
        group = groupRepositoryAdapter.get(group.id()).orElseThrow();

        //Version 4
        groupRepositoryAdapter.save(group.withSuperGroup(digit));
        group = groupRepositoryAdapter.get(group.id()).orElseThrow();

        final Group group1 = group;

        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(
                        group1
                                .withPrettyName(new PrettyName("new digIT'18"))
                                .withVersion(3)
                ));

        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(
                        group1
                                .withGroupMembers(List.of(gm(u1, chair)))
                                .withVersion(-500)
                ));

        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(
                        group1
                                .withAvatarUri(Optional.of(new ImageUri("myimage.png")))
                                .withVersion(500)
                ));

        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> groupRepositoryAdapter.save(
                        group1
                                .withSuperGroup(digit)
                                .withVersion(0)
                ));

        assertThatNoException()
                .isThrownBy(() -> groupRepositoryAdapter.save(group1
                        .withVersion(5) //Expect version after 4 saves.
                        .withPrettyName(new PrettyName("new digIT'18")))
                );
    }

    @Test
    public void Given_InvalidGroup_Expect_delete_To_Throw() {
        assertThatExceptionOfType(GroupRepository.GroupNotFoundException.class)
                .isThrownBy(() -> groupRepositoryAdapter.delete(GroupId.generate()));
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAll_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(digit18, digit19, prit19, styrit19);

        assertThat(groupRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        asSaved(removeLockedUsers(digit18)),
                        asSaved(removeLockedUsers(digit19)),
                        asSaved(removeLockedUsers(prit19)),
                        asSaved(removeLockedUsers(styrit19))
                );
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllBySuperGroup_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(digit17, digit18, digit19, prit18, prit19, styrit18, styrit19, drawit18);

        assertThat(groupRepositoryAdapter.getAllBySuperGroup(digit.id()))
                .containsExactlyInAnyOrder(asSaved(removeLockedUsers(digit19)));
        assertThat(groupRepositoryAdapter.getAllBySuperGroup(didit.id()))
                .containsExactlyInAnyOrder(asSaved(removeLockedUsers(digit17)), asSaved(removeLockedUsers(digit18)));
        assertThat(groupRepositoryAdapter.getAllBySuperGroup(drawit.id()))
                .isEmpty();
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllByPost_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(digit17, digit18, digit19, prit18);

        assertThat(groupRepositoryAdapter.getAllByPost(chair.id()))
                .containsExactlyInAnyOrder(asSaved(removeLockedUsers(digit17)), asSaved(removeLockedUsers(digit18)), asSaved(removeLockedUsers(digit19)), asSaved(removeLockedUsers(prit18)));
        assertThat(groupRepositoryAdapter.getAllByPost(member.id()))
                .containsExactlyInAnyOrder(asSaved(removeLockedUsers(digit17)), asSaved(removeLockedUsers(digit19)), asSaved(removeLockedUsers(prit18)));
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllByUser_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(digit17, digit18, digit19, prit18, prit19, styrit18, styrit19, drawit18, drawit19);

        assertThat(groupRepositoryAdapter.getAllByUser(u0.id()))
                .isEmpty();
        assertThat(groupRepositoryAdapter.getAllByUser(u1.id()))
                .containsExactlyInAnyOrder(
                        new UserMembership(chair.withVersion(1), asSaved(removeLockedUsers(digit18)), new UnofficialPostName("root")),
                        new UserMembership(chair.withVersion(1), asSaved(removeLockedUsers(prit18)), new UnofficialPostName("ChefChef")),
                        new UserMembership(treasurer.withVersion(1), asSaved(removeLockedUsers(prit18)), UnofficialPostName.none()),
                        new UserMembership(chair.withVersion(1), asSaved(removeLockedUsers(drawit19)), UnofficialPostName.none())
                );
        assertThat(groupRepositoryAdapter.getAllByUser(u3.id()))
                .containsExactlyInAnyOrder(
                        new UserMembership(chair.withVersion(1), asSaved(removeLockedUsers(digit19)), UnofficialPostName.none())
                );
    }

    private void addGroup(Group... groups) throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        DomainUtils.addGroup(
                superGroupTypeRepository,
                superGroupRepository,
                userRepository,
                postRepository,
                groupRepositoryAdapter,
                groups
        );
    }

}
