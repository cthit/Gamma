package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static it.chalmers.gamma.DomainFactory.addAll;
import static it.chalmers.gamma.DomainFactory.alumni;
import static it.chalmers.gamma.DomainFactory.board;
import static it.chalmers.gamma.DomainFactory.chair;
import static it.chalmers.gamma.DomainFactory.committee;
import static it.chalmers.gamma.DomainFactory.didit;
import static it.chalmers.gamma.DomainFactory.digit;
import static it.chalmers.gamma.DomainFactory.digit18;
import static it.chalmers.gamma.DomainFactory.digit19;
import static it.chalmers.gamma.DomainFactory.emeritus;
import static it.chalmers.gamma.DomainFactory.member;
import static it.chalmers.gamma.DomainFactory.prit;
import static it.chalmers.gamma.DomainFactory.prit18;
import static it.chalmers.gamma.DomainFactory.prit19;
import static it.chalmers.gamma.DomainFactory.sprit;
import static it.chalmers.gamma.DomainFactory.styrit;
import static it.chalmers.gamma.DomainFactory.styrit18;
import static it.chalmers.gamma.DomainFactory.styrit19;
import static it.chalmers.gamma.DomainFactory.treasurer;
import static it.chalmers.gamma.DomainFactory.u0;
import static it.chalmers.gamma.DomainFactory.u1;
import static it.chalmers.gamma.DomainFactory.u10;
import static it.chalmers.gamma.DomainFactory.u11;
import static it.chalmers.gamma.DomainFactory.u2;
import static it.chalmers.gamma.DomainFactory.u3;
import static it.chalmers.gamma.DomainFactory.u4;
import static it.chalmers.gamma.DomainFactory.u5;
import static it.chalmers.gamma.DomainFactory.u6;
import static it.chalmers.gamma.DomainFactory.u7;
import static it.chalmers.gamma.DomainFactory.u8;
import static it.chalmers.gamma.DomainFactory.u9;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.AUTHORITY;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.GROUP;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.SUPERGROUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ActiveProfiles("test")
@DataJpaTest
@Import({AuthorityLevelRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        UserRepositoryAdapter.class,
        UserEntityConverter.class,
        PostRepositoryAdapter.class,
        PostEntityConverter.class,
        GroupRepositoryAdapter.class,
        GroupEntityConverter.class,
        SuperGroupTypeRepositoryAdapter.class,
        AuthorityLevelEntityConverter.class,
        SuperGroupEntityConverter.class,
        UserEntityConverter.class,
        PostEntityConverter.class})
public class AuthorityLevelEntityIntegrationTests {

    @Autowired
    private AuthorityLevelRepositoryAdapter authorityLevelRepositoryAdapter;

    @Autowired
    private SuperGroupRepository superGroupRepository;

    @Autowired
    private SuperGroupTypeRepository superGroupTypeRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * This is a very important test! If it fails, please stop.
     */
    @Test
    public void getByUser_SuperTest() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        addAll(userRepository, u0, u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11);

        superGroupTypeRepository.add(committee);
        superGroupTypeRepository.add(alumni);
        superGroupTypeRepository.add(board);

        postRepository.save(chair);
        postRepository.save(treasurer);
        postRepository.save(member);

        superGroupRepository.save(digit);
        superGroupRepository.save(didit);
        superGroupRepository.save(prit);
        superGroupRepository.save(sprit);
        superGroupRepository.save(styrit);
        superGroupRepository.save(emeritus);

        groupRepository.save(digit18);
        groupRepository.save(digit19);
        groupRepository.save(prit18);
        groupRepository.save(prit19);
        groupRepository.save(styrit18);
        groupRepository.save(styrit19);

        AuthorityLevelName adminName = new AuthorityLevelName("admin");
        AuthorityLevel adminLevel = new AuthorityLevel(
                adminName,
                List.of(new AuthorityLevel.SuperGroupPost(digit, chair), new AuthorityLevel.SuperGroupPost(didit, chair)),
                List.of(styrit),
                List.of(u1)
        );

        AuthorityLevelName matName = new AuthorityLevelName("mat");
        AuthorityLevel matLevel = new AuthorityLevel(
                matName,
                Collections.emptyList(),
                List.of(prit),
                List.of(u5, u8)
        );

        AuthorityLevelName glassName = new AuthorityLevelName("glass");
        AuthorityLevel glassLevel = new AuthorityLevel(
                glassName,
                List.of(new AuthorityLevel.SuperGroupPost(styrit, chair)),
                Collections.emptyList(),
                Collections.emptyList()
        );

        this.authorityLevelRepositoryAdapter.create(adminName);
        this.authorityLevelRepositoryAdapter.save(adminLevel);

        this.authorityLevelRepositoryAdapter.create(matName);
        this.authorityLevelRepositoryAdapter.save(matLevel);

        this.authorityLevelRepositoryAdapter.create(glassName);
        this.authorityLevelRepositoryAdapter.save(glassLevel);

        assertThat(this.authorityLevelRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(adminLevel, matLevel, glassLevel);

        assertThat(this.authorityLevelRepositoryAdapter.get(adminName))
                .isNotEmpty()
                .get().isEqualTo(adminLevel);
        assertThat(this.authorityLevelRepositoryAdapter.get(matName))
                .isNotEmpty()
                .get().isEqualTo(matLevel);
        assertThat(this.authorityLevelRepositoryAdapter.get(glassName))
                .isNotEmpty()
                .get().isEqualTo(glassLevel);

        AuthorityLevelName sprit = new AuthorityLevelName("sprit");
        AuthorityLevelName prit = new AuthorityLevelName("prit");
        AuthorityLevelName prit18 = new AuthorityLevelName("prit18");
        AuthorityLevelName prit19 = new AuthorityLevelName("prit19");
        AuthorityLevelName didit = new AuthorityLevelName("didit");
        AuthorityLevelName digit = new AuthorityLevelName("digit");
        AuthorityLevelName digit18 = new AuthorityLevelName("digit18");
        AuthorityLevelName digit19 = new AuthorityLevelName("digit19");
        AuthorityLevelName emeritus = new AuthorityLevelName("emeritus");
        AuthorityLevelName styrit = new AuthorityLevelName("styrit");
        AuthorityLevelName styrit18 = new AuthorityLevelName("styrit18");
        AuthorityLevelName styrit19 = new AuthorityLevelName("styrit19");

        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u0.id()))
                .isEmpty();
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u1.id()))
                .containsExactlyInAnyOrder(
                        ua(adminName, AUTHORITY),
                        ua(sprit, SUPERGROUP),
                        ua(didit, SUPERGROUP),
                        ua(prit18, GROUP),
                        ua(digit18, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u2.id()))
                .containsExactlyInAnyOrder(
                        ua(sprit, SUPERGROUP),
                        ua(didit, SUPERGROUP),
                        ua(digit, SUPERGROUP),
                        ua(digit19, GROUP),
                        ua(digit18, GROUP),
                        ua(prit18, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u3.id()))
                .containsExactlyInAnyOrder(
                        ua(adminName, AUTHORITY),
                        ua(digit, SUPERGROUP),
                        ua(digit19, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u4.id()))
                .containsExactlyInAnyOrder(
                        ua(digit, SUPERGROUP),
                        ua(digit19, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u5.id()))
                .containsExactlyInAnyOrder(
                        ua(matName, AUTHORITY),
                        ua(prit, SUPERGROUP),
                        ua(prit19, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u6.id()))
                .containsExactlyInAnyOrder(
                        ua(matName, AUTHORITY),
                        ua(prit, SUPERGROUP),
                        ua(prit19, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u7.id()))
                .containsExactlyInAnyOrder(
                        ua(emeritus, SUPERGROUP),
                        ua(styrit18, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u8.id()))
                .containsExactlyInAnyOrder(
                        ua(emeritus, SUPERGROUP),
                        ua(styrit18, GROUP),
                        ua(matName, AUTHORITY)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u9.id()))
                .containsExactlyInAnyOrder(
                        ua(emeritus, SUPERGROUP),
                        ua(styrit18, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u10.id()))
                .containsExactlyInAnyOrder(
                        ua(glassName, AUTHORITY),
                        ua(adminName, AUTHORITY),
                        ua(styrit, SUPERGROUP),
                        ua(styrit19, GROUP)
                );
        assertThat(this.authorityLevelRepositoryAdapter.getByUser(u11.id()))
                .containsExactlyInAnyOrder(
                        ua(adminName, AUTHORITY),
                        ua(styrit, SUPERGROUP),
                        ua(styrit19, GROUP)
                );
    }

    private static UserAuthority ua(AuthorityLevelName name, AuthorityType authorityType) {
        return new UserAuthority(name, authorityType);
    }

    @Test
    public void Given_TwoAuthorityLevels_Expect_create_To_Work() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");
        AuthorityLevelName mat = new AuthorityLevelName("mat");

        authorityLevelRepositoryAdapter.create(admin);
        authorityLevelRepositoryAdapter.create(mat);

        assertThat(authorityLevelRepositoryAdapter.getAll().stream())
                .extracting(AuthorityLevel::name)
                .contains(admin, mat);
    }

    @Test
    public void Given_NewAuthorityLevel_Expect_create_To_CreateEmptyAuthorityLevel() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);

        assertThat(authorityLevelRepositoryAdapter.get(admin))
                .isNotEmpty()
                .get().isEqualTo(new AuthorityLevel(
                        admin,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));
    }

    @Test
    public void Given_TwoAuthorityLevelWithSameName_Expect_create_To_Throw() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelAlreadyExistsException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.create(admin));

        assertThat(authorityLevelRepositoryAdapter.get(admin))
                .isNotEmpty();
    }

    @Test
    public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroup_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelConstraintViolationRuntimeException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                        new AuthorityLevel(
                                admin,
                                Collections.emptyList(),
                                List.of(digit),
                                Collections.emptyList()
                        )
                ));
    }

    @Test
    public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroupPost_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelConstraintViolationRuntimeException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                        new AuthorityLevel(
                                admin,
                                List.of(new AuthorityLevel.SuperGroupPost(digit, chair)),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ));
    }

    @Test
    public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidUser_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelConstraintViolationRuntimeException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                        new AuthorityLevel(
                                admin,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                List.of(u1)
                        )
                ));
    }

    @Test
    public void Given_AValidAuthorityLevelThatHasNotBeenSaved_Expect_save_To_Throw() {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelNotFoundRuntimeException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                        new AuthorityLevel(
                                admin,
                                List.of(new AuthorityLevel.SuperGroupPost(digit, chair)),
                                List.of(digit),
                                List.of(u1)
                )));
    }

    @Test
    public void Given_AValidAuthorityLevel_Expect_delete_To_Work() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        authorityLevelRepositoryAdapter.create(admin);
        assertThatNoException()
                .isThrownBy(() -> authorityLevelRepositoryAdapter.delete(admin));
    }

    @Test
    public void Given_NoAuthorityLevel_Expect_delete_To_Throw() {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        assertThatExceptionOfType(AuthorityLevelRepository.AuthorityLevelNotFoundException.class)
                .isThrownBy(() -> authorityLevelRepositoryAdapter.delete(admin));
    }

}
