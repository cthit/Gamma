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

@ActiveProfiles("test")
@Import({
  ClientAuthorityRepositoryAdapter.class,
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
  ClientAuthorityEntityConverter.class,
  SuperGroupEntityConverter.class,
  UserEntityConverter.class,
  UserAccessGuard.class,
  PostEntityConverter.class,
  SettingsRepositoryAdapter.class
})
public class AuthorityEntityIntegrationTests extends AbstractEntityIntegrationTests {

  /*

  @Autowired
  private ClientAuthorityRepositoryAdapter authorityLevelRepositoryAdapter;
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

  private static UserAuthority ua(AuthorityName name, AuthorityType authorityType) {
      return new UserAuthority(name, authorityType);
  }

  @BeforeEach
  public void setSettings() {
      this.settingsRepository.setSettings(defaultSettings);
  }

  @BeforeEach
  public void clearSecurityContext() {
      SecurityContextHolder.clearContext();
  }

  // This is a very important test! If it fails, please stop.
  @Test
  public void getByUser_SuperTest() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
      addAll(userRepository, u0, u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11);
      addAll(superGroupTypeRepository, committee, alumni, board);
      addAll(postRepository, chair, treasurer, member);
      addAll(superGroupRepository, digit, didit, prit, sprit, styrit, emeritus);
      addAll(groupRepository, digit18, digit19, prit18, prit19, styrit18, styrit19);

      AuthorityName adminName = new AuthorityName("admin");
      Authority adminLevel = new Authority(
              adminName,
              List.of(new Authority.SuperGroupPost(digit, chair), new Authority.SuperGroupPost(didit, chair)),
              List.of(styrit),
              List.of(u1)
      );

      AuthorityName matName = new AuthorityName("mat");
      Authority matLevel = new Authority(
              matName,
              Collections.emptyList(),
              List.of(prit),
              List.of(u5, u8)
      );

      AuthorityName glassName = new AuthorityName("glass");
      Authority glassLevel = new Authority(
              glassName,
              List.of(new Authority.SuperGroupPost(styrit, chair)),
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

      AuthorityName sprit = new AuthorityName("sprit");
      AuthorityName prit = new AuthorityName("prit");
      AuthorityName prit18 = new AuthorityName("prit18");
      AuthorityName prit19 = new AuthorityName("prit19");
      AuthorityName didit = new AuthorityName("didit");
      AuthorityName digit = new AuthorityName("digit");
      AuthorityName digit18 = new AuthorityName("digit18");
      AuthorityName digit19 = new AuthorityName("digit19");
      AuthorityName emeritus = new AuthorityName("emeritus");
      AuthorityName styrit = new AuthorityName("styrit");
      AuthorityName styrit18 = new AuthorityName("styrit18");
      AuthorityName styrit19 = new AuthorityName("styrit19");

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

  @Test
  public void Given_TwoAuthorityLevels_Expect_create_To_Work() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");
      AuthorityName mat = new AuthorityName("mat");

      authorityLevelRepositoryAdapter.create(admin);
      authorityLevelRepositoryAdapter.create(mat);

      assertThat(authorityLevelRepositoryAdapter.getAll().stream())
              .extracting(Authority::name)
              .containsExactlyInAnyOrder(admin, mat);
  }

  @Test
  public void Given_NewAuthorityLevel_Expect_create_To_CreateEmptyAuthorityLevel() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      authorityLevelRepositoryAdapter.create(admin);

      assertThat(authorityLevelRepositoryAdapter.get(admin))
              .isNotEmpty()
              .get().isEqualTo(new Authority(
                      admin,
                      Collections.emptyList(),
                      Collections.emptyList(),
                      Collections.emptyList()
              ));
  }

  @Test
  public void Given_TwoAuthorityLevelWithSameName_Expect_create_To_Throw() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      authorityLevelRepositoryAdapter.create(admin);
      assertThatExceptionOfType(ClientAuthorityRepository.AuthorityLevelAlreadyExistsException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.create(admin));

      assertThat(authorityLevelRepositoryAdapter.get(admin))
              .isNotEmpty();
  }

  @Test
  public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroup_To_Throw()
          throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      authorityLevelRepositoryAdapter.create(admin);
      assertThatExceptionOfType(ClientAuthorityRepository.NotCompleteAuthorityLevelException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                      new Authority(
                              admin,
                              Collections.emptyList(),
                              List.of(digit),
                              Collections.emptyList()
                      )
              ));
  }

  @Test
  public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroupPost_Specifically_NoPost_To_Throw()
          throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      superGroupTypeRepository.add(digit.type());
      superGroupRepository.save(digit);

      authorityLevelRepositoryAdapter.create(admin);
      assertThatExceptionOfType(ClientAuthorityRepository.NotCompleteAuthorityLevelException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                      new Authority(
                              admin,
                              List.of(new Authority.SuperGroupPost(digit, chair)),
                              Collections.emptyList(),
                              Collections.emptyList()
                      )
              ));
  }

  @Test
  public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidSuperGroupPost_Specifically_NoSuperGroup_To_Throw()
          throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      postRepository.save(chair);

      authorityLevelRepositoryAdapter.create(admin);
      assertThatExceptionOfType(ClientAuthorityRepository.NotCompleteAuthorityLevelException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                      new Authority(
                              admin,
                              List.of(new Authority.SuperGroupPost(digit, chair)),
                              Collections.emptyList(),
                              Collections.emptyList()
                      )
              ));
  }

  @Test
  public void Given_AValidAuthorityLevel_Expect_save_With_AInvalidUser_To_Throw()
          throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      authorityLevelRepositoryAdapter.create(admin);
      assertThatExceptionOfType(ClientAuthorityRepository.NotCompleteAuthorityLevelException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                      new Authority(
                              admin,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              List.of(u1)
                      )
              ));
  }

  @Test
  public void Given_AValidAuthorityLevelThatHasNotBeenSaved_Expect_save_To_Throw() {
      AuthorityName admin = new AuthorityName("admin");

      assertThatExceptionOfType(ClientAuthorityRepository.AuthorityLevelNotFoundRuntimeException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.save(
                      new Authority(
                              admin,
                              List.of(new Authority.SuperGroupPost(digit, chair)),
                              List.of(digit),
                              List.of(u1)
                      )));
  }

  @Test
  public void Given_AValidAuthorityLevel_Expect_delete_To_Work() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
      AuthorityName admin = new AuthorityName("admin");

      authorityLevelRepositoryAdapter.create(admin);
      assertThatNoException()
              .isThrownBy(() -> authorityLevelRepositoryAdapter.delete(admin));
  }

  @Test
  public void Given_NoAuthorityLevel_Expect_delete_To_Throw() {
      AuthorityName admin = new AuthorityName("admin");

      assertThatExceptionOfType(ClientAuthorityRepository.AuthorityLevelNotFoundException.class)
              .isThrownBy(() -> authorityLevelRepositoryAdapter.delete(admin));
  }

  */

}
