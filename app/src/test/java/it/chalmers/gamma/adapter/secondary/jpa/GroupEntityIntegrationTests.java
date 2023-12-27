package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityRepositoryAdapter;
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
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsAdminUser;

@ActiveProfiles("test")
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
        SettingsRepositoryAdapter.class,
        ClientAuthorityRepositoryAdapter.class,
        ClientAuthorityEntityConverter.class})
public class GroupEntityIntegrationTests extends AbstractEntityIntegrationTests {

    /*
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
    @Autowired
    private ClientAuthorityRepository clientAuthorityRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_ValidGroup_Expect_save_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);
        Group groupToSave = digit18;
        addGroup(groupToSave);

        Group savedGroup = groupRepositoryAdapter.get(groupToSave.id())
                .orElseThrow();

        assertThat(savedGroup)
                .isEqualTo(asSaved(groupToSave));
    }

    @Test
    public void Given_SameGroupIdTwice_Expect_save_To_Throw() {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);

        assertThatExceptionOfType(MutableEntity.StaleDomainObjectException.class)
                .isThrownBy(() -> addGroup(digit18, digit18));

        assertThat(groupRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        asSaved(digit18)
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
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);

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
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);

        addGroup(digit18, digit19, prit19, styrit19);

        assertThat(groupRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        asSaved(digit18),
                        asSaved(digit19),
                        asSaved(prit19),
                        asSaved(styrit19)
                );
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllBySuperGroup_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);
        addGroup(digit17, digit18, digit19, prit18, prit19, styrit18, styrit19, drawit18);

        assertThat(groupRepositoryAdapter.getAllBySuperGroup(digit.id()))
                .containsExactlyInAnyOrder(asSaved(digit19));
        assertThat(groupRepositoryAdapter.getAllBySuperGroup(didit.id()))
                .containsExactlyInAnyOrder(asSaved(digit17), asSaved(digit18));
        assertThat(groupRepositoryAdapter.getAllBySuperGroup(drawit.id()))
                .isEmpty();
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllByPost_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);
        addGroup(digit17, digit18, digit19, prit18);

        assertThat(groupRepositoryAdapter.getAllByPost(chair.id()))
                .containsExactlyInAnyOrder(asSaved(digit17), asSaved(digit18), asSaved(digit19), asSaved(prit18));
        assertThat(groupRepositoryAdapter.getAllByPost(member.id()))
                .containsExactlyInAnyOrder(asSaved(digit17), asSaved(digit19), asSaved(prit18));
    }

    @Test
    public void Given_MultipleValidGroups_Expect_getAllByUser_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);
        addGroup(digit17, digit18, digit19, prit18, prit19, styrit18, styrit19, drawit18, drawit19);

        assertThat(groupRepositoryAdapter.getAllByUser(u0.id()))
                .isEmpty();
        assertThat(groupRepositoryAdapter.getAllByUser(u1.id()))
                .containsExactlyInAnyOrder(
                        new UserMembership(chair.withVersion(1), asSaved(digit18), new UnofficialPostName("root")),
                        new UserMembership(chair.withVersion(1), asSaved(prit18), new UnofficialPostName("ChefChef")),
                        new UserMembership(treasurer.withVersion(1), asSaved(prit18), UnofficialPostName.none()),
                        new UserMembership(chair.withVersion(1), asSaved(drawit19), UnofficialPostName.none())
                );
        assertThat(groupRepositoryAdapter.getAllByUser(u3.id()))
                .containsExactlyInAnyOrder(
                        new UserMembership(chair.withVersion(1), asSaved(digit19), UnofficialPostName.none())
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

    */

}