package it.chalmers.gamma.app.authentication;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.WebServerRedirectUrl;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.password.PasswordService;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class AccessGuardTest {

    @Mock
    private AuthenticatedService authenticatedService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private AuthorityLevelRepository authorityLevelRepository;

    @InjectMocks
    private AccessGuard accessGuard;

    private static final User adminUser = new User(
            UserId.generate(),
            0,
            new Cid("abcde"),
            new Email("abcde@chalmers.it"),
            Language.EN,
            new Nick("TheAdmin"),
            new Password("{noop}password123"),
            new FirstName("Something"),
            new LastName("Somethingsson"),
            Instant.now(),
            new AcceptanceYear(2021),
            true,
            false,
            Optional.empty()
    );

    private static final User normalUser = new User(
            UserId.generate(),
            0,
            new Cid("edcba"),
            new Email("edcba@chalmers.it"),
            Language.EN,
            new Nick("TheUser"),
            new Password("{noop}password321"),
            new FirstName("Something1"),
            new LastName("Somethingsson1"),
            Instant.now(),
            new AcceptanceYear(2021),
            true,
            false,
            Optional.empty()
    );

    private static final Post member = new Post(
            PostId.generate(),
            0,
            new Text(
                    "Ledamot",
                    "Member"
            ),
            new EmailPrefix("ledamot")
    );

    private static final SuperGroupType committee = new SuperGroupType("committee");

    private static final SuperGroup digIT = new SuperGroup(
            SuperGroupId.generate(),
            0,
            new Name("digit"),
            new PrettyName("digIT"),
            committee,
            new Text(
                    "Hanterar sektionens digitala system",
                    "Manages the student divisions digital systems"
            )
    );

    private static final Group digIT18 = new Group(
            GroupId.generate(),
            0,
            new Name("digit18"),
            new PrettyName("digIT' 18"),
            digIT,
            Collections.singletonList(
                    new GroupMember(
                            member,
                            new UnofficialPostName("ServerChef"),
                            normalUser
                    )
            ),
            Optional.empty(),
            Optional.empty()
    );

    private static final Group digIT19 = new Group(
            GroupId.generate(),
            0,
            new Name("digit19"),
            new PrettyName("digIT' 19"),
            digIT,
            Collections.emptyList(),
            Optional.empty(),
            Optional.empty()
    );

    private static final AuthorityLevelName adminAuthority = new AuthorityLevelName("admin");
    private static final AuthorityLevelName matAuthority = new AuthorityLevelName("mat");
    private static final AuthorityLevelName digitAuthority = new AuthorityLevelName(digIT.name().value());
    private static final AuthorityLevelName digIt18Authority = new AuthorityLevelName(digIT18.name().value());

    private static final Map<UserId, List<UserAuthority>> userAuthoritiesMap = new HashMap<>() {{
        put(normalUser.id(), List.of(
                new UserAuthority(matAuthority, AuthorityType.AUTHORITY),
                new UserAuthority(digitAuthority, AuthorityType.SUPERGROUP),
                new UserAuthority(digIt18Authority, AuthorityType.GROUP)
        ));
        put(adminUser.id(), List.of(
                new UserAuthority(adminAuthority, AuthorityType.AUTHORITY)
        ));
    }};

    private static final ApiKey clientApiKey = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("My api key"),
            new Text(
                    "Det här är min api nyckel",
                    "This is my api key"
            ),
            ApiKeyType.CLIENT,
            ApiKeyToken.generate()
    );

    private static final Client client = new Client(
            ClientUid.generate(),
            ClientId.generate(),
            ClientSecret.generate(),
            new WebServerRedirectUrl("https://mat.chalmers.it"),
            new PrettyName("Mat client"),
            new Text(
                    "Det här är mat klienten",
                    "This is the mat client"
            ),
            Collections.emptyList(),
            Collections.singletonList(Scope.PROFILE),
            Collections.singletonList(normalUser),
            Optional.of(clientApiKey)
    );

    private static final ApiKey chalmersitApi = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("chalmers.it api key"),
            new Text(
                    "chalmers.it api nyckel",
                    "chalmers.it api key"
            ),
            ApiKeyType.INFO,
            ApiKeyToken.generate()
    );

    private static final ApiKey goldappsitApi = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("goldapps api key"),
            new Text(
                    "goldapps api nyckel",
                    "goldapps api key"
            ),
            ApiKeyType.GOLDAPPS,
            ApiKeyToken.generate()
    );

    private static final ApiKey whitelistApi = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("whitelist api key"),
            new Text(
                    "whitelist api nyckel",
                    "whitelist api key"
            ),
            ApiKeyType.WHITELIST,
            ApiKeyToken.generate()
    );

    private static final ApiAuthenticated clientApiAuthenticated = new ApiAuthenticated() {
        @Override
        public ApiKey get() {
            return clientApiKey;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.of(client);
        }
    };

    private static final ApiAuthenticated chalmersitApiAuthenticated = new ApiAuthenticated() {
        @Override
        public ApiKey get() {
            return chalmersitApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };

    private static final ApiAuthenticated goldappsApiAuthenticated = new ApiAuthenticated() {
        @Override
        public ApiKey get() {
            return goldappsitApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };

    private static final ApiAuthenticated whitelistApiAuthenticated = new ApiAuthenticated() {
        @Override
        public ApiKey get() {
            return whitelistApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };

    @Test
    public void Given_Admin_Expect_isAdmin_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isAdmin()));
    }

    @Test
    public void Given_NonAdmin_Expect_isAdmin_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);
        given(authorityLevelRepository.getByUser(normalUser.id()))
                .willReturn(userAuthoritiesMap.get(normalUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isAdmin()));
    }

    @Test
    public void Given_Unauthenticated_Expect_isAdmin_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(new Unauthenticated() {});

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isAdmin()));
    }

    /**
     * External authenticated admins should not be able to be abused by a client
     * to do admin stuff.
     */
    @Test
    public void Given_ExternalAuthenticatedAdmin_Expect_isAdmin_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((ExternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isAdmin()));
    }

    @Test
    public void Given_Api_Expect_isAdmin_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isAdmin()));
    }

    @Test
    public void Given_AdminAndCorrectPassword_Expect_isAdmin_To_NotThrow() {
        String password = "password";

        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));
        given(passwordService.matches(new UnencryptedPassword(password), adminUser.password()))
                .willReturn(true);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.requireAll(
                        isAdmin(),
                        passwordCheck(password)
                ));
    }

    @Test
    public void Given_AdminAndIncorrectPassword_Expect_isAdmin_To_Throw() {
        String password = "wrongpassword";

        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));
        given(passwordService.matches(new UnencryptedPassword(password), adminUser.password()))
                .willReturn(false);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.requireAll(
                        isAdmin(),
                        passwordCheck(password)
                ));
    }

    @Test
    public void Given_ClientApi_Expect_isClientApi_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isClientApi()));
    }

    @Test
    public void Given_ClientApiWithNormalUserApproved_Expect_userHasAcceptedClient_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.requireAll(
                        isClientApi(),
                        userHasAcceptedClient(normalUser.id())
                ));
        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.requireAll(
                        isClientApi(),
                        userHasAcceptedClient(adminUser.id())
                ));
    }

    @Test
    public void Given_ClientApiWithNormalUserApproved_Expect_userHasAcceptedClient_With_AdminUser_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.requireAll(
                        isClientApi(),
                        userHasAcceptedClient(adminUser.id())
                ));
    }

    @Test
    public void Given_UserIsSignedIn_Expect_isNotSignedIn_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isNotSignedIn()));
    }

    @Test
    public void Given_UserNotSignedIn_Expect_isNotSignedIn_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn(new Unauthenticated() {});

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isNotSignedIn()));
    }

    @Test
    public void Given_UserIsSignedIn_Expect_isSignedIn_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isSignedIn()));
    }

    @Test
    public void Given_UserNotSignedIn_Expect_isSignedIn_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(new Unauthenticated() {});

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isSignedIn()));
    }

    @Test
    public void Given_UserIsInGroup_Expect_isSignedInUserMemberOfGroup_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isSignedInUserMemberOfGroup(digIT18)));
    }

    @Test
    public void Given_UserIsNotInGroup_Expect_isSignedInUserMemberOfGroup_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isSignedInUserMemberOfGroup(digIT18)));
    }

    @Test
    public void Given_CorrectApiType_Expect_isApi_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));

        given(authenticatedService.getAuthenticated())
                .willReturn(chalmersitApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.INFO)));

        given(authenticatedService.getAuthenticated())
                .willReturn(goldappsApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS)));

        given(authenticatedService.getAuthenticated())
                .willReturn(whitelistApiAuthenticated);

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.WHITELIST)));
    }

    @Test
    public void Given_IncorrectApiType_Expect_isApi_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.WHITELIST)));

        given(authenticatedService.getAuthenticated())
                .willReturn(chalmersitApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));

        given(authenticatedService.getAuthenticated())
                .willReturn(goldappsApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.INFO)));

        given(authenticatedService.getAuthenticated())
                .willReturn(whitelistApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS)));
    }

    @Test
    public void Given_IsAdmin_Expect_isApi_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));
    }

    @Test
    public void Given_NeitherChecksIsValid_Expect_requireEither_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);
        given(authorityLevelRepository.getByUser(normalUser.id()))
                .willReturn(userAuthoritiesMap.get(normalUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.requireEither(
                        isAdmin(),
                        isSignedInUserMemberOfGroup(digIT19)
                ));
    }

    @Test
    public void Given_OneOfTwoChecksIsValid_Expect_requireEither_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> normalUser);
        given(authorityLevelRepository.getByUser(normalUser.id()))
                .willReturn(userAuthoritiesMap.get(normalUser.id()));

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.requireEither(
                        isAdmin(),
                        isSignedInUserMemberOfGroup(digIT18)
                ));
    }

    @Test
    public void Given_IsApi_Expect_passwordCheck_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(clientApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(passwordCheck("password")));
    }

    @Test
    public void Given_IsAdmin_Expect_isClientApi_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((InternalUserAuthenticated) () -> adminUser);
        given(authorityLevelRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isClientApi()));
    }

    @Test
    public void Given_ExternalUser_Expect_isSignedInUserMemberOfGroup_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn((ExternalUserAuthenticated) () -> normalUser);
        given(authorityLevelRepository.getByUser(normalUser.id()))
                .willReturn(userAuthoritiesMap.get(normalUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isSignedInUserMemberOfGroup(digIT18)));
    }

    @Test
    public void Given_IsChalmersITApi_Expect_userHasAcceptedClient_To_Throw() {
        given(authenticatedService.getAuthenticated())
                .willReturn(chalmersitApiAuthenticated);

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(userHasAcceptedClient(adminUser.id())));
    }

    @Test
    public void Given_BootstrapAuthenticatedAdmin_Expect_isLocalRunner_To_NotThrow() {
        given(authenticatedService.getAuthenticated())
                .willReturn(new LocalRunnerAuthenticated() { });

        assertThatNoException()
                .isThrownBy(() -> this.accessGuard.require(isLocalRunner()));
    }

}