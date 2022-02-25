package it.chalmers.gamma.app.group;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.utils.DomainUtils;
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
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static it.chalmers.gamma.utils.DomainUtils.addAll;
import static it.chalmers.gamma.utils.DomainUtils.asSaved;
import static it.chalmers.gamma.utils.DomainUtils.chair;
import static it.chalmers.gamma.utils.DomainUtils.committee;
import static it.chalmers.gamma.utils.DomainUtils.defaultSettings;
import static it.chalmers.gamma.utils.DomainUtils.digit;
import static it.chalmers.gamma.utils.DomainUtils.digit18;
import static it.chalmers.gamma.utils.DomainUtils.gm;
import static it.chalmers.gamma.utils.DomainUtils.member;
import static it.chalmers.gamma.utils.DomainUtils.treasurer;
import static it.chalmers.gamma.utils.DomainUtils.u1;
import static it.chalmers.gamma.utils.DomainUtils.u3;
import static it.chalmers.gamma.utils.DomainUtils.u4;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsAdminUser;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GroupFacade.class,
        GroupRepositoryAdapter.class,
        GroupEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserAccessGuard.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        PostRepositoryAdapter.class,
        PostEntityConverter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        SuperGroupTypeRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        SettingsRepositoryAdapter.class,
        AuthorityLevelRepositoryAdapter.class,
        AuthorityLevelEntityConverter.class
})
public class GroupFacadeIntegrationTest {

    @MockBean
    private AccessGuard accessGuard;
    @Autowired
    private GroupFacade groupFacade;
    @Autowired
    private GroupRepository groupRepository;
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
    private AuthorityLevelRepository authorityLevelRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_Group_Expect_create_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupFacade.GroupAlreadyExistsException, GroupFacade.SuperGroupNotFoundRuntimeException {
        superGroupTypeRepository.add(committee);
        superGroupRepository.save(digit);

        GroupFacade.NewGroup newGroup = new GroupFacade.NewGroup(
                "mygroup",
                "My Group",
                digit.id().value()
        );

        groupFacade.create(newGroup);

        List<GroupFacade.GroupDTO> groups = groupFacade.getAll();

        assertThat(groups)
                .hasSize(1);

        GroupFacade.GroupDTO groupDTO = groups.get(0);
        SuperGroup superGroup = this.superGroupRepository.get(new SuperGroupId(newGroup.superGroup())).orElseThrow();

        assertThat(groupDTO)
                .hasNoNullFieldsOrProperties()
                .isEqualTo(new GroupFacade.GroupDTO(
                        groupDTO.id(),
                        newGroup.name(),
                        newGroup.prettyName(),
                        new SuperGroupFacade.SuperGroupDTO(superGroup)
                ));
    }

    @Test
    public void Given_Group_Expect_update_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException, GroupFacade.GroupAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, authorityLevelRepository);

        Group group = digit18;
        SuperGroup newSuperGroup = digit;

        superGroupTypeRepository.add(newSuperGroup.type());
        superGroupRepository.save(newSuperGroup);

        addGroup(group);

        GroupFacade.UpdateGroup updateGroup = new GroupFacade.UpdateGroup(
                group.id().value(),
                group.version() + 1,
                "newdigit18",
                "new digIT'18",
                newSuperGroup.id().value()
        );

        this.groupFacade.update(updateGroup);

        GroupFacade.GroupWithMembersDTO groupDTO = this.groupFacade.getWithMembers(group.id().value()).orElseThrow();
        assertThat(groupDTO)
                .isEqualTo(new GroupFacade.GroupWithMembersDTO(
                        group.with()
                                .name(new Name("newdigit18"))
                                .prettyName(new PrettyName("new digIT'18"))
                                .superGroup(newSuperGroup.withVersion(1))
                                .version(2)
                                .groupMembers(group.groupMembers()
                                        .stream()
                                        //If the user is locked, remove
//                                        .filter(groupMember -> !(groupMember.user().extended().locked() || !groupMember.user().extended().acceptedUserAgreement()) )
                                        .map(groupMember -> new GroupMember(
                                                groupMember.post().withVersion(1),
                                                groupMember.unofficialPostName(),
                                                asSaved(groupMember.user()))
                                        ).toList()
                                )
                                .build()
                ));

    }

    @Test
    public void Given_GroupWithNewMembers_Expect_setMembers_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        setAuthenticatedAsAdminUser(userRepository, authorityLevelRepository);
        postRepository.save(member);
        addAll(userRepository, u3, u4);
        addGroup(digit18);

        List<GroupFacade.ShallowMember> newMembers = List.of(
                new GroupFacade.ShallowMember(u1.id().value(), chair.id().value(), null),
                new GroupFacade.ShallowMember(u3.id().value(), member.id().value(), "ServerChef"),
                new GroupFacade.ShallowMember(u4.id().value(), treasurer.id().value(), "DubbelAnsvarig"),
                new GroupFacade.ShallowMember(u4.id().value(), member.id().value(), null)
        );

        this.groupFacade.setMembers(digit18.id().value(), newMembers);

        Group expectedGroup = digit18.with()
                .version(1)
                .groupMembers(List.of(
                        gm(u1, chair),
                        gm(u3, member, new UnofficialPostName("ServerChef")),
                        gm(u4, treasurer, new UnofficialPostName("DubbelAnsvarig")),
                        gm(u4, member)
                )).build();

        //setMembers increases by one.
        expectedGroup = asSaved(expectedGroup);
        GroupFacade.GroupWithMembersDTO expectedGroupDTO = new GroupFacade.GroupWithMembersDTO(expectedGroup);

        assertThat(this.groupFacade.getWithMembers(digit18.id().value()))
                .get()
                .isEqualTo(expectedGroupDTO);
    }

    private void addGroup(Group... groups) throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        DomainUtils.addGroup(
                superGroupTypeRepository,
                superGroupRepository,
                userRepository,
                postRepository,
                groupRepository,
                groups
        );
    }

}
