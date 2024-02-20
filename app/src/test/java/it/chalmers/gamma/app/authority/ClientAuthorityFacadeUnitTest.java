package it.chalmers.gamma.app.authority;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ClientAuthorityFacadeUnitTest {
  /*
      @Mock
      private AccessGuard accessGuard;

      @Mock
      private ClientAuthorityRepository clientAuthorityRepository;

      @Mock
      private UserRepository userRepository;

      @Mock
      private PostRepository postRepository;

      @Mock
      private SuperGroupRepository superGroupRepository;

      @InjectMocks
      private ClientAuthorityFacade clientAuthorityFacade;

      private static Authority.SuperGroupPost sgp(SuperGroup sg, Post p) {
          return new Authority.SuperGroupPost(sg, p);
      }

      @BeforeEach
      public void clearSecurityContext() {
          SecurityContextHolder.clearContext();
      }

      @Test
      public void Given_AValidName_Expect_create_To_CreateValidAuthorityLevel() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
          UUID clientUid = UUID.randomUUID();
          String clientAuthority = "mat";

          clientAuthorityFacade.create(clientUid, clientAuthority);

          ArgumentCaptor<AuthorityName> captor = ArgumentCaptor.forClass(AuthorityName.class);
          verify(clientAuthorityRepository).create(captor.capture());
          AuthorityName authorityName = captor.getValue();

          assertThat(authorityName)
                  .isEqualTo(new AuthorityName(adminAuthorityLevelName));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).create(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_VariousInvalidNames_Expect_create_To_Throw() {
          assertThatNullPointerException()
                  .isThrownBy(() -> clientAuthorityFacade.create(null));

          //Too short
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.create("a"));

          //Uppercase
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.create("A"));

          //Too long
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.create("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
      }

      @Test
      public void Given_AValidName_Expect_delete_To_DeleteAuthorityLevel()
              throws ClientAuthorityFacade.AuthorityLevelNotFoundException,
              ClientAuthorityRepository.AuthorityLevelNotFoundException {
          String adminAuthorityLevelName = "admin";

          clientAuthorityFacade.delete(adminAuthorityLevelName);

          ArgumentCaptor<AuthorityName> captor = ArgumentCaptor.forClass(AuthorityName.class);
          verify(clientAuthorityRepository).delete(captor.capture());
          AuthorityName authorityName = captor.getValue();

          assertThat(authorityName)
                  .isEqualTo(new AuthorityName(adminAuthorityLevelName));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).delete(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_VariousInvalidNames_Expect_delete_To_Throw() throws ClientAuthorityRepository.AuthorityLevelNotFoundException {
          assertThatNullPointerException()
                  .isThrownBy(() -> clientAuthorityFacade.delete(null));

          //Too short
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.delete("a"));

          //Uppercase
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.delete("A"));

          //Too long
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.delete("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

          //Illegal characters
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.delete("ö$a"));

          //Legal name, but doesn't exist

          doThrow(ClientAuthorityRepository.AuthorityLevelNotFoundException.class)
                  .when(clientAuthorityRepository).delete(any());

          assertThatExceptionOfType(ClientAuthorityFacade.AuthorityLevelNotFoundException.class)
                  .isThrownBy(() -> clientAuthorityFacade.delete("hello"));
      }

      @Test
      public void Given_AValidName_Expect_get_To_ReturnAuthorityLevel() {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(
                          sgp(
                                  styrit,
                                  chair
                          )
                  ),
                  List.of(
                          digit
                  ),
                  List.of(u1, u2, u3)
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          Optional<ClientAuthorityFacade.ClientAuthorityDTO> authorityLevelDTO = clientAuthorityFacade.get("admin");

          ArgumentCaptor<AuthorityName> captor = ArgumentCaptor.forClass(AuthorityName.class);
          verify(clientAuthorityRepository).get(captor.capture());
          AuthorityName authorityName = captor.getValue();

          assertThat(authorityName)
                  .isEqualTo(new AuthorityName("admin"));

          AuthorityLevelDTOAssert.assertThat(authorityLevelDTO)
                  .isEqualTo(adminAuthority);

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_VariousInvalidNames_Expect_get_To_Throw() {
          assertThatNullPointerException()
                  .isThrownBy(() -> clientAuthorityFacade.get(null));

          //Too short
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.get("a"));

          //Uppercase
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.get("A"));

          //Too long
          assertThatIllegalArgumentException()
                  .isThrownBy(() -> clientAuthorityFacade.get("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
      }

      @Test
      public void MakeSureGetAllWorksProperly() {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(sgp(styrit, chair)),
                  List.of(digit),
                  List.of(u1, u2, u3)
          );

          Authority matAuthority = new Authority(
                  new AuthorityName("mat"),
                  List.of(sgp(digit, chair)),
                  Collections.emptyList(),
                  List.of(u5, u6)
          );

          Authority bookitAuthority = new Authority(
                  new AuthorityName("bookit"),
                  Collections.emptyList(),
                  List.of(prit),
                  List.of(u8, u9)
          );

          List<Authority> authorities = List.of(adminAuthority, matAuthority, bookitAuthority);

          given(clientAuthorityRepository.getAll())
                  .willReturn(authorities);

          List<ClientAuthorityFacade.ClientAuthorityDTO> clientAuthorityDTOS = this.clientAuthorityFacade.getAll();

          Assertions.assertThat(clientAuthorityDTOS)
                  .zipSatisfy(authorities, (actual, expected) ->
                          AuthorityLevelDTOAssert.assertThat(actual)
                                  .isEqualTo(expected));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).getAll();
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_EmptyAuthorityLevel_Expect_addSuperGroupToAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException, ClientAuthorityFacade.SuperGroupNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  Collections.emptyList(),
                  Collections.emptyList(),
                  Collections.emptyList()
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(superGroupRepository.get(digit.id()))
                  .willReturn(Optional.of(digit));

          this.clientAuthorityFacade.addSuperGroupToAuthorityLevel("admin", digit.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          Collections.emptyList(),
                          List.of(digit),
                          Collections.emptyList()
                  ));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository, superGroupRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(superGroupRepository).get(digit.id());
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_FilledAuthorityLevel_Expect_addSuperGroupToAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException, ClientAuthorityFacade.SuperGroupNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                  List.of(drawit, dragit),
                  List.of(u2, u3, u4)
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(superGroupRepository.get(digit.id()))
                  .willReturn(Optional.of(digit));

          this.clientAuthorityFacade.addSuperGroupToAuthorityLevel("admin", digit.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                          List.of(drawit, dragit, digit),
                          List.of(u2, u3, u4)
                  ));
      }

      @Test
      public void Given_EmptyAuthorityLevel_Expect_addSuperGroupPostToAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException, ClientAuthorityFacade.PostNotFoundException, ClientAuthorityFacade.SuperGroupNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  Collections.emptyList(),
                  Collections.emptyList(),
                  Collections.emptyList()
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(superGroupRepository.get(digit.id()))
                  .willReturn(Optional.of(digit));

          given(postRepository.get(chair.id()))
                  .willReturn(Optional.of(chair));

          this.clientAuthorityFacade.addSuperGroupPostToAuthorityLevel(
                  "admin",
                  digit.id().value(),
                  chair.id().value()
          );

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          List.of(sgp(digit, chair)),
                          Collections.emptyList(),
                          Collections.emptyList()
                  ));

          InOrder inOrder = inOrder(
                  accessGuard,
                  clientAuthorityRepository,
                  superGroupRepository,
                  postRepository
          );

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(superGroupRepository).get(digit.id());
          inOrder.verify(postRepository).get(chair.id());
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_FilledAuthorityLevel_Expect_addSuperGroupPostToAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException, ClientAuthorityFacade.PostNotFoundException, ClientAuthorityFacade.SuperGroupNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                  List.of(drawit, dragit),
                  List.of(u2, u3, u4)
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(superGroupRepository.get(digit.id()))
                  .willReturn(Optional.of(digit));

          given(postRepository.get(chair.id()))
                  .willReturn(Optional.of(chair));

          this.clientAuthorityFacade.addSuperGroupPostToAuthorityLevel(
                  "admin",
                  digit.id().value(),
                  chair.id().value()
          );

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          List.of(sgp(styrit, chair), sgp(emeritus, chair), sgp(digit, chair)),
                          List.of(drawit, dragit),
                          List.of(u2, u3, u4)
                  ));
      }

      @Test
      public void Given_EmptyAuthorityLevel_Expect_addUserToAuthorityLevel_To_Work() throws ClientAuthorityFacade.UserNotFoundException, ClientAuthorityFacade.AuthorityLevelNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  Collections.emptyList(),
                  Collections.emptyList(),
                  Collections.emptyList()
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(userRepository.get(u1.id()))
                  .willReturn(Optional.of(u1));

          this.clientAuthorityFacade.addUserToAuthorityLevel("admin", u1.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          Collections.emptyList(),
                          Collections.emptyList(),
                          List.of(u1)
                  ));

          InOrder inOrder = inOrder(
                  accessGuard,
                  clientAuthorityRepository,
                  userRepository
          );

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).requireEither(isAdmin(), isLocalRunner());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(userRepository).get(u1.id());
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_FilledAuthorityLevel_Expect_addUserToAuthorityLevel_To_Work() throws ClientAuthorityFacade.UserNotFoundException, ClientAuthorityFacade.AuthorityLevelNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                  List.of(drawit, dragit),
                  List.of(u2, u3, u4)
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          given(userRepository.get(u1.id()))
                  .willReturn(Optional.of(u1));

          this.clientAuthorityFacade.addUserToAuthorityLevel("admin", u1.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                          List.of(drawit, dragit),
                          List.of(u2, u3, u4, u1)
                  ));
      }

      @Test
      public void Given_AuthorityLevelWithSuperGroup_Expect_removeSuperGroupFromAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  Collections.emptyList(),
                  List.of(digit),
                  Collections.emptyList()
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          this.clientAuthorityFacade.removeSuperGroupFromAuthorityLevel("admin", digit.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          Collections.emptyList(),
                          Collections.emptyList(),
                          Collections.emptyList()
                  ));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository, superGroupRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_AuthorityLevelWithSuperGroupPost_Expect_removeSuperGroupPostFromAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  List.of(sgp(digit, chair)),
                  Collections.emptyList(),
                  Collections.emptyList()
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          this.clientAuthorityFacade.removeSuperGroupPostFromAuthorityLevel("admin", digit.id().value(), chair.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          Collections.emptyList(),
                          Collections.emptyList(),
                          Collections.emptyList()
                  ));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository, superGroupRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      @Test
      public void Given_AuthorityLevelWithUser_Expect_removeUserFromAuthorityLevel_To_Work() throws ClientAuthorityFacade.AuthorityLevelNotFoundException {
          Authority adminAuthority = new Authority(
                  new AuthorityName("admin"),
                  Collections.emptyList(),
                  Collections.emptyList(),
                  List.of(u1)
          );

          given(clientAuthorityRepository.get(new AuthorityName("admin")))
                  .willReturn(Optional.of(adminAuthority));

          this.clientAuthorityFacade.removeUserFromAuthorityLevel("admin", u1.id().value());

          ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
          verify(clientAuthorityRepository).save(captor.capture());
          Authority newAuthority = captor.getValue();

          Assertions.assertThat(newAuthority)
                  .isEqualTo(new Authority(
                          new AuthorityName("admin"),
                          Collections.emptyList(),
                          Collections.emptyList(),
                          Collections.emptyList()
                  ));

          InOrder inOrder = inOrder(accessGuard, clientAuthorityRepository, superGroupRepository);

          //Makes sure that isAdmin is called first
          inOrder.verify(accessGuard).require(isAdmin());
          inOrder.verify(clientAuthorityRepository).get(new AuthorityName("admin"));
          inOrder.verify(clientAuthorityRepository).save(any());
          inOrder.verifyNoMoreInteractions();
      }

      public static class AuthorityLevelDTOAssert extends AbstractAssert<AuthorityLevelDTOAssert, ClientAuthorityFacade.ClientAuthorityDTO> {

          protected AuthorityLevelDTOAssert(ClientAuthorityFacade.ClientAuthorityDTO actual) {
              super(actual, AuthorityLevelDTOAssert.class);
          }

          public static AuthorityLevelDTOAssert assertThat(ClientAuthorityFacade.ClientAuthorityDTO clientAuthorityDTO) {
              return new AuthorityLevelDTOAssert(clientAuthorityDTO);
          }

          public static AuthorityLevelDTOAssert assertThat(Optional<ClientAuthorityFacade.ClientAuthorityDTO> actual) {
              if (actual.isEmpty()) {
                  fail("Optional is empty");
                  return null;
              }
              return new AuthorityLevelDTOAssert(actual.get());
          }


          public AuthorityLevelDTOAssert isEqualTo(Authority authority) {
              isNotNull();

              Assertions.assertThat(actual)
                      .hasOnlyFields("clientAuthority", "superGroups", "users", "posts");

              Assertions.assertThat(actual.clientAuthority())
                      .isEqualTo(authority.name().value());

              Assertions.assertThat(actual.superGroups()).zipSatisfy(authority.superGroups(), (actual, expected) ->
                      Assertions.assertThat(actual)
                              .isEqualTo(new SuperGroupFacade.SuperGroupDTO(expected))
              );

              Assertions.assertThat(actual.users()).zipSatisfy(authority.users(), (actual, expected) ->
                      Assertions.assertThat(actual)
                              .isEqualTo(new UserFacade.UserDTO(expected))
              );

              Assertions.assertThat(actual.posts()).zipSatisfy(authority.posts(), (actual, expected) -> {
                  Assertions.assertThat(actual.superGroup())
                          .isEqualTo(new SuperGroupFacade.SuperGroupDTO(expected.superGroup()));
                  Assertions.assertThat(actual.post())
                          .isEqualTo(new PostFacade.PostDTO(expected.post()));
              });

              return this;
          }
      }
  */
}
