package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
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
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static it.chalmers.gamma.utils.DomainUtils.addAll;
import static it.chalmers.gamma.utils.DomainUtils.alumni;
import static it.chalmers.gamma.utils.DomainUtils.asSaved;
import static it.chalmers.gamma.utils.DomainUtils.board;
import static it.chalmers.gamma.utils.DomainUtils.chair;
import static it.chalmers.gamma.utils.DomainUtils.committee;
import static it.chalmers.gamma.utils.DomainUtils.defaultSettings;
import static it.chalmers.gamma.utils.DomainUtils.didit;
import static it.chalmers.gamma.utils.DomainUtils.digit;
import static it.chalmers.gamma.utils.DomainUtils.digit18;
import static it.chalmers.gamma.utils.DomainUtils.digit19;
import static it.chalmers.gamma.utils.DomainUtils.emeritus;
import static it.chalmers.gamma.utils.DomainUtils.member;
import static it.chalmers.gamma.utils.DomainUtils.prit;
import static it.chalmers.gamma.utils.DomainUtils.prit18;
import static it.chalmers.gamma.utils.DomainUtils.prit19;
import static it.chalmers.gamma.utils.DomainUtils.sprit;
import static it.chalmers.gamma.utils.DomainUtils.styrit;
import static it.chalmers.gamma.utils.DomainUtils.styrit18;
import static it.chalmers.gamma.utils.DomainUtils.styrit19;
import static it.chalmers.gamma.utils.DomainUtils.treasurer;
import static it.chalmers.gamma.utils.DomainUtils.u0;
import static it.chalmers.gamma.utils.DomainUtils.u1;
import static it.chalmers.gamma.utils.DomainUtils.u10;
import static it.chalmers.gamma.utils.DomainUtils.u11;
import static it.chalmers.gamma.utils.DomainUtils.u2;
import static it.chalmers.gamma.utils.DomainUtils.u3;
import static it.chalmers.gamma.utils.DomainUtils.u4;
import static it.chalmers.gamma.utils.DomainUtils.u5;
import static it.chalmers.gamma.utils.DomainUtils.u6;
import static it.chalmers.gamma.utils.DomainUtils.u7;
import static it.chalmers.gamma.utils.DomainUtils.u8;
import static it.chalmers.gamma.utils.DomainUtils.u9;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.AUTHORITY;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.GROUP;
import static it.chalmers.gamma.app.authoritylevel.domain.AuthorityType.SUPERGROUP;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AuthorityLevelRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        PostRepositoryAdapter.class,
        PostEntityConverter.class,
        GroupRepositoryAdapter.class,
        GroupEntityConverter.class,
        SuperGroupTypeRepositoryAdapter.class,
        AuthorityLevelEntityConverter.class,
        SuperGroupEntityConverter.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        PostEntityConverter.class,
        SettingsRepositoryAdapter.class})
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
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    /**
     * This is a very important test! If it fails, please stop.
     */
    @Test
    public void getByUser_SuperTest() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addAll(userRepository, u0, u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11);
        addAll(superGroupTypeRepository, committee, alumni, board);
        addAll(postRepository, chair, treasurer, member);
        addAll(superGroupRepository, digit, didit, prit, sprit, styrit, emeritus);
        addAll(groupRepository, digit18, digit19, prit18, prit19, styrit18, styrit19);

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

        setAuthenticatedUser(null, null, u1, true);

        this.authorityLevelRepositoryAdapter.create(matName);
        this.authorityLevelRepositoryAdapter.save(matLevel);

        this.authorityLevelRepositoryAdapter.create(glassName);
        this.authorityLevelRepositoryAdapter.save(glassLevel);

        adminLevel = asSaved(adminLevel);
        matLevel = asSaved(matLevel);
        glassLevel = asSaved(glassLevel);

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
                .containsExactlyInAnyOrder(admin, mat);
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
        assertThatExceptionOfType(AuthorityLevelRepository.SuperGroupNotFoundRuntimeException.class)
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
    public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroupPost_Specifically_NoPost_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        superGroupTypeRepository.add(digit.type());
        superGroupRepository.save(digit);

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.SuperGroupPostNotFoundRuntimeException.class)
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
    public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroupPost_Specifically_NoSuperGroup_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        postRepository.save(chair);

        authorityLevelRepositoryAdapter.create(admin);
        assertThatExceptionOfType(AuthorityLevelRepository.SuperGroupPostNotFoundRuntimeException.class)
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
        assertThatExceptionOfType(AuthorityLevelRepository.UserNotFoundRuntimeException.class)
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
