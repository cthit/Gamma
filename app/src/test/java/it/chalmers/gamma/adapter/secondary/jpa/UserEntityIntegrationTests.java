package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.utils.PasswordEncoderTestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({
  UserRepositoryAdapter.class,
  UserEntityConverter.class,
  UserAccessGuard.class,
  ClientAuthorityRepositoryAdapter.class,
  ClientAuthorityEntityConverter.class,
  SuperGroupEntityConverter.class,
  PostEntityConverter.class,
  SettingsRepositoryAdapter.class,
  PasswordEncoderTestConfiguration.class
})
public class UserEntityIntegrationTests extends AbstractEntityIntegrationTests {

  /*
  @Autowired
  private UserRepositoryAdapter userRepositoryAdapter;
  @Autowired
  private ClientAuthorityRepositoryAdapter authorityLevelRepositoryAdapter;

  @Autowired
  private SettingsRepository settingsRepository;

  @BeforeEach
  public void clearSecurityContext() {
      SecurityContextHolder.clearContext();
  }


  @BeforeEach
  public void setupSettings() {
      this.settingsRepository.setSettings(new Settings(
              Instant.ofEpochSecond(0),
              Collections.emptyList()
      ));
  }

  @Test
  public void Given_ValidNewUser_Expect_create_To_Work() {
      GammaUser user = setAuthenticatedAsNormalUser(userRepositoryAdapter);

      assertThat(this.userRepositoryAdapter.get(user.id()))
              .get()
              .isEqualTo(user.withExtended(user.extended().withVersion(1)));
  }

  @Test
  public void Given_UserWithoutExtended_Expect_create_To_Throw() {
      GammaUser user = DEFAULT_USER.withExtended(null);

      assertThatIllegalStateException()
              .isThrownBy(() -> this.userRepositoryAdapter.create(
                      user,
                      new UnencryptedPassword("password")
              ));
  }

  @Test
  public void Given_ValidUser_Expect_save_To_Work() {
      //Will save the user
      GammaUser user = setAuthenticatedAsNormalUser(userRepositoryAdapter);

      GammaUser newUser = user.with()
              .nick(new Nick("Smurf_RandoM"))
              .acceptanceYear(new AcceptanceYear(2020))
              .build();

      this.userRepositoryAdapter.save(newUser);

      assertThat(this.userRepositoryAdapter.get(DEFAULT_USER.id()))
              .get()
              .isEqualTo(newUser.withExtended(newUser.extended().withVersion(2)));
  }

  @Test
  public void Given_UserWithNewUserId_Expect_save_To_Throw() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      GammaUser user = setAuthenticatedAsNormalUser(userRepositoryAdapter);
      this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
      GammaUser loadedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
      assertThatExceptionOfType(MutableEntity.IllegalEntityStateException.class)
              .isThrownBy(() -> this.userRepositoryAdapter.save(loadedUser.withId(UserId.generate())));
  }

  @Test
  public void Given_ValidUser_Expect_get_To_HaveAUpdatedVersion() {
      UserId userId = setAuthenticatedAsNormalUser(userRepositoryAdapter).id();
      GammaUser loadedUser = this.userRepositoryAdapter.get(userId).orElseThrow();
      assertThat(loadedUser.extended().version())
              .isEqualTo(1);
  }

  @Test
  public void Given_UserWithoutExtended_Expect_save_To_Throw() {
      GammaUser user = DEFAULT_USER.withExtended(null);

      assertThatIllegalStateException()
              .isThrownBy(() -> this.userRepositoryAdapter.create(
                      user,
                      new UnencryptedPassword("password")
              ));
  }

  @Test
  public void Given_User_Expect_setPassword_and_checkPassword_To_Work() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      GammaUser user = DEFAULT_USER;
      this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
      assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("password")))
              .isTrue();

      this.userRepositoryAdapter.setPassword(user.id(), new UnencryptedPassword("new_password"));
      assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("new_password")))
              .isTrue();
  }

  @Test
  public void Given_User_Expect_setPassword_and_wrong_checkPassword_To_Return_False() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      GammaUser user = DEFAULT_USER;
      this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
      assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("_password")))
              .isFalse();

      this.userRepositoryAdapter.setPassword(user.id(), new UnencryptedPassword("new_password"));
      assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("new_password_")))
              .isFalse();
  }

  @Test
  public void Given_ValidUser_Expect_acceptUserAgreement_To_Work() {
      GammaUser user = DEFAULT_USER.withExtended(DEFAULT_USER.extended().withAcceptedUserAgreement(false));

      setAuthenticatedUser(
              userRepositoryAdapter,
              authorityLevelRepositoryAdapter,
              user,
              false
      );

      GammaUser updatedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
      assertThat(updatedUser.extended().acceptedUserAgreement())
              .isFalse();

      this.userRepositoryAdapter.acceptUserAgreement(user.id());
      updatedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
      assertThat(updatedUser.extended().acceptedUserAgreement())
              .isTrue();
  }

  @Test
  public void Given_OneUserTryingToAccessLockedUser_Expect_get_To_Return_Null() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      setAuthenticatedAsNormalUser(userRepositoryAdapter);

      GammaUser lockedUser = new GammaUser(
              UserId.generate(),
              new Cid("hmmm"),
              new Nick("SomethinG"),
              new FirstName("Smurf"),
              new LastName("Smurfsson"),
              new AcceptanceYear(2018),
              Language.EN,
              new UserExtended(
                      new Email("somthing@chalmers.it"),
                      0,
                      true,
                      false,
                      true,
                      ImageUri.defaultUserAvatar()
              )
      );

      this.userRepositoryAdapter.create(lockedUser, new UnencryptedPassword("password"));

      assertThat(this.userRepositoryAdapter.get(lockedUser.id()))
              .isEmpty();
  }

  @Test
  public void Given_OneUserTryingToAccessUserThatHaveNotAcceptedUserAgreement_Expect_get_To_Return_Null() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      setAuthenticatedAsNormalUser(userRepositoryAdapter);

      GammaUser userThatHasNotAcceptedUserAgreement = new GammaUser(
              UserId.generate(),
              new Cid("hmmm"),
              new Nick("SomethinG"),
              new FirstName("Smurf"),
              new LastName("Smurfsson"),
              new AcceptanceYear(2018),
              Language.EN,
              new UserExtended(
                      new Email("somthing@chalmers.it"),
                      0,
                      false,
                      false,
                      false,
                      ImageUri.defaultUserAvatar()
              )
      );

      this.userRepositoryAdapter.create(userThatHasNotAcceptedUserAgreement, new UnencryptedPassword("password"));

      assertThat(this.userRepositoryAdapter.get(userThatHasNotAcceptedUserAgreement.id()))
              .isEmpty();
  }

  @Test
  public void Given_AdminTryingToAccessUserThatHaveNotAcceptedUserAgreement_Expect_get_To_Work() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      setAuthenticatedAsAdminUser(userRepositoryAdapter, authorityLevelRepositoryAdapter);

      GammaUser userThatHasNotAcceptedUserAgreement = new GammaUser(
              UserId.generate(),
              new Cid("hmmm"),
              new Nick("SomethinG"),
              new FirstName("Smurf"),
              new LastName("Smurfsson"),
              new AcceptanceYear(2018),
              Language.EN,
              new UserExtended(
                      new Email("somthing@chalmers.it"),
                      0,
                      false,
                      false,
                      false,
                      ImageUri.defaultUserAvatar()
              )
      );

      this.userRepositoryAdapter.create(userThatHasNotAcceptedUserAgreement, new UnencryptedPassword("password"));

      assertThat(this.userRepositoryAdapter.get(userThatHasNotAcceptedUserAgreement.id()))
              .get().isEqualTo(
                      userThatHasNotAcceptedUserAgreement
                              .withExtended(userThatHasNotAcceptedUserAgreement.extended().withVersion(1))
              );
  }

  @Test
  public void Given_AdminTryingToAccessLockedUser_Expect_get_To_Work() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      setAuthenticatedAsAdminUser(userRepositoryAdapter, authorityLevelRepositoryAdapter);

      GammaUser lockedUser = new GammaUser(
              UserId.generate(),
              new Cid("hmmm"),
              new Nick("SomethinG"),
              new FirstName("Smurf"),
              new LastName("Smurfsson"),
              new AcceptanceYear(2018),
              Language.EN,
              new UserExtended(
                      new Email("somthing@chalmers.it"),
                      0,
                      true,
                      false,
                      true,
                      ImageUri.defaultUserAvatar()
              )
      );

      this.userRepositoryAdapter.create(lockedUser, new UnencryptedPassword("password"));

      assertThat(this.userRepositoryAdapter.get(lockedUser.id()))
              .get().isEqualTo(
                      lockedUser
                              .withExtended(lockedUser.extended().withVersion(1))
              );
  }

  @Test
  public void Given_TwoUsersWithSameCid_Expect_create_To_Throw() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      this.userRepositoryAdapter.create(DEFAULT_USER, new UnencryptedPassword("password"));

      assertThatExceptionOfType(UserRepository.CidAlreadyInUseException.class)
              .isThrownBy(() -> this.userRepositoryAdapter.create(
                      DEFAULT_USER.with()
                              .id(UserId.generate())
                              .extended(DEFAULT_USER.extended().with()
                                      .email(new Email("lmao@chalmers.it"))
                                      .build()
                              )
                              .build(),
                      new UnencryptedPassword("password")
              ));
  }

  @Test
  public void Given_TwoUsersWithSameEmail_Expect_create_To_Throw() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
      this.userRepositoryAdapter.create(DEFAULT_USER, new UnencryptedPassword("password"));

      assertThatExceptionOfType(UserRepository.EmailAlreadyInUseException.class)
              .isThrownBy(() -> this.userRepositoryAdapter.create(
                      DEFAULT_USER.with()
                              .id(UserId.generate())
                              .cid(new Cid("somethi"))
                              .build(),
                      new UnencryptedPassword("password")
              ));
  }

   */
}
