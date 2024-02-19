package it.chalmers.gamma.app.authentication;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class AccessGuardTest {

    /*
    private static final GammaUser adminUser = new GammaUser(
            UserId.generate(),
            new Cid("edcba"),
            new Nick("TheAdmin"),
            new FirstName("Something1"),
            new LastName("Somethingsson1"),
            new AcceptanceYear(2021),
            Language.EN,
            new UserExtended(
                    new Email("edcba@chalmers.it"),
                    0,
                    true,
                    false,
                    false,
                    null
            )
    );
    private static final GammaUser normalUser = new GammaUser(
            UserId.generate(),
            new Cid("edcba"),
            new Nick("TheUser"),
            new FirstName("Something1"),
            new LastName("Somethingsson1"),
            new AcceptanceYear(2021),
            Language.EN,
            new UserExtended(
                    new Email("edcba@chalmers.it"),
                    0,
                    true,
                    false,
                    false,
                    null
            )
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
    private static final AuthorityName digIt18Authority = new AuthorityName(digIT18.name().value());
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
    private static final AuthorityName digitAuthority = new AuthorityName(digIT.name().value());
    private static final AuthorityName adminAuthority = new AuthorityName("admin");
    private static final AuthorityName matAuthority = new AuthorityName("mat");
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
            new ClientRedirectUrl("https://mat.chalmers.it"),
            new PrettyName("Mat client"),
            new Text(
                    "Det här är mat klienten",
                    "This is the mat client"
            ),
            Collections.singletonList(Scope.PROFILE),
            clientApiKey,
            new ClientOwnerOfficial()
    );
    private static final ApiAuthentication CLIENT_API_DETAILS = new ApiAuthentication() {
        @Override
        public ApiKey get() {
            return clientApiKey;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.of(client);
        }
    };
    private static final ApiKey infoApi = new ApiKey(
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
    private static final ApiKey allowListApi = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("allow list api key"),
            new Text(
                    "allow list api nyckel",
                    "allow list api key"
            ),
            ApiKeyType.ALLOW_LIST,
            ApiKeyToken.generate()
    );
    private static final ApiAuthentication INFO_API_DETAILS = new ApiAuthentication() {
        @Override
        public ApiKey get() {
            return infoApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };
    private static final ApiAuthentication GOLDAPPS_API_DETAILS = new ApiAuthentication() {
        @Override
        public ApiKey get() {
            return goldappsitApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };
    private static final ApiAuthentication ALLOW_LIST_API_DETAILS = new ApiAuthentication() {
        @Override
        public ApiKey get() {
            return allowListApi;
        }

        @Override
        public Optional<Client> getClient() {
            return Optional.empty();
        }
    };
    private static final UserAuthentication adminAuthenticated = new UserAuthentication() {
        @Override
        public GammaUser get() {
            return adminUser;
        }

        @Override
        public List<UserAuthority> getAuthorities() {
            return Collections.singletonList(new UserAuthority(new AuthorityName("admin"), AuthorityType.AUTHORITY));
        }

    };
    private static final UserAuthentication normalUserAuthenticated = new UserAuthentication() {
        @Override
        public GammaUser get() {
            return normalUser;
        }

        @Override
        public List<UserAuthority> getAuthorities() {
            return Collections.emptyList();
        }
    };
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientAuthorityRepository clientAuthorityRepository;
    @InjectMocks
    private AccessGuard accessGuard;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void Given_Admin_Expect_isAdmin_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
            given(clientAuthorityRepository.getByUser(adminUser.id()))
                    .willReturn(userAuthoritiesMap.get(adminUser.id()));

            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isAdmin()));
        }
    }

    @Test
    public void Given_AdminThatIsLocked_Expect_isAdmin_To_Throw() {
        GammaUser lockedAdminUser = adminUser.with()
                .id(UserId.generate())
                .extended(adminUser.extended().withLocked(true))
                .build();

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(lockedAdminUser));
            given(clientAuthorityRepository.getByUser(lockedAdminUser.id()))
                    .willReturn(userAuthoritiesMap.get(lockedAdminUser.id()));
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isAdmin()));
        }
    }

    @Test
    public void Given_NonAdmin_Expect_isAdmin_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
            given(clientAuthorityRepository.getByUser(normalUser.id()))
                    .willReturn(userAuthoritiesMap.get(normalUser.id()));

            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isAdmin()));
        }
    }

    @Test
    public void Given_Unauthenticated_Expect_isAdmin_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(new SecurityContextImpl());

            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isAdmin()));
        }
    }

    @Test
    public void Given_Api_Expect_isAdmin_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isAdmin()));
        }
    }

    @Test
    public void Given_AdminAndCorrectPassword_Expect_isAdmin_To_NotThrow() {
        String password = "password";
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
            given(clientAuthorityRepository.getByUser(adminUser.id()))
                    .willReturn(userAuthoritiesMap.get(adminUser.id()));
            given(userRepository.checkPassword(adminUser.id(), new UnencryptedPassword(password)))
                    .willReturn(true);

            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.requireAll(
                            isAdmin(),
                            passwordCheck(password)
                    ));
        }


    }

    @Test
    public void Given_AdminAndIncorrectPassword_Expect_isAdmin_To_Throw() {
        String password = "wrongpassword";

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
            given(clientAuthorityRepository.getByUser(adminUser.id()))
                    .willReturn(userAuthoritiesMap.get(adminUser.id()));
            given(userRepository.checkPassword(adminUser.id(), new UnencryptedPassword(password)))
                    .willReturn(false);

            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.requireAll(
                            isAdmin(),
                            passwordCheck(password)
                    ));
        }


    }

    @Test
    public void Given_ClientApi_Expect_isClientApi_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isClientApi()));
        }
    }

    @Test
    public void Given_ClientApiWithNormalUserApproved_Expect_userHasAcceptedClient_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
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

    }

    @Test
    public void Given_ClientApiWithNormalUserApproved_Expect_userHasAcceptedClient_With_AdminUser_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.requireAll(
                            isClientApi(),
                            userHasAcceptedClient(adminUser.id())
                    ));
        }

    }

    @Test
    public void Given_UserIsSignedIn_Expect_isNotSignedIn_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isNotSignedIn()));
        }

    }

    @Test
    public void Given_UserNotSignedIn_Expect_isNotSignedIn_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(new SecurityContextImpl());
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isNotSignedIn()));
        }

    }

    @Test
    public void Given_UserIsSignedIn_Expect_isSignedIn_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isSignedIn()));
        }

    }

    @Test
    public void Given_UserIsSignedInAndLocked_Expect_isSignedIn_To_NotThrow() {
        GammaUser lockedUser = normalUser.with()
                .id(UserId.generate())
                .extended(normalUser.extended().withLocked(true))
                .build();

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(lockedUser));
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isSignedIn()));
        }

    }

    @Test
    public void Given_UserNotSignedIn_Expect_isSignedIn_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(new SecurityContextImpl());
            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(isSignedIn()));
        }

    }

    @Test
    public void Given_UserIsInGroup_Expect_isSignedInUserMemberOfGroup_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isSignedInUserMemberOfGroup(digIT18)));
        }

    }

    @Test
    public void Given_UserIsNotInGroup_Expect_isSignedInUserMemberOfGroup_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isSignedInUserMemberOfGroup(digIT18)));
    }

    @Test
    public void Given_CorrectApiType_Expect_isApi_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));
        }

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(INFO_API_DETAILS));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.INFO)));
        }

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(GOLDAPPS_API_DETAILS));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS)));
        }

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(ALLOW_LIST_API_DETAILS));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.ALLOW_LIST)));
        }
    }

    @Test
    public void Given_IncorrectApiType_Expect_isApi_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.ALLOW_LIST)));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(INFO_API_DETAILS));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(GOLDAPPS_API_DETAILS));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.INFO)));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(ALLOW_LIST_API_DETAILS));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS)));
    }

    @Test
    public void Given_IsAdmin_Expect_isApi_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
        }

        given(clientAuthorityRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isApi(ApiKeyType.CLIENT)));
    }

    @Test
    public void Given_NeitherChecksIsValid_Expect_requireEither_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
        }

        given(clientAuthorityRepository.getByUser(normalUser.id()))
                .willReturn(userAuthoritiesMap.get(normalUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.requireEither(
                        isAdmin(),
                        isSignedInUserMemberOfGroup(digIT19)
                ));
    }

    @Test
    public void Given_OneOfTwoChecksIsValid_Expect_requireEither_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(normalUserAuthenticated));
            given(clientAuthorityRepository.getByUser(normalUser.id()))
                    .willReturn(userAuthoritiesMap.get(normalUser.id()));
            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.requireEither(
                            isAdmin(),
                            isSignedInUserMemberOfGroup(digIT18)
                    ));
        }
    }

    @Test
    public void Given_IsApi_Expect_passwordCheck_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(CLIENT_API_DETAILS));
        }

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(passwordCheck("password")));
    }

    @Test
    public void Given_IsAdmin_Expect_isClientApi_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(adminAuthenticated));
        }

        given(clientAuthorityRepository.getByUser(adminUser.id()))
                .willReturn(userAuthoritiesMap.get(adminUser.id()));

        assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                .isThrownBy(() -> this.accessGuard.require(isClientApi()));
    }

    @Test
    public void Given_IsInfoApi_Expect_userHasAcceptedClient_To_Throw() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(INFO_API_DETAILS));

            assertThatExceptionOfType(AccessGuard.AccessDeniedException.class)
                    .isThrownBy(() -> this.accessGuard.require(userHasAcceptedClient(adminUser.id())));
        }

    }

    @Test
    public void Given_BootstrapAuthenticatedAdmin_Expect_isLocalRunner_To_NotThrow() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext)
                    .thenReturn(wrapDetails(new LocalRunnerAuthentication() {
                    }));

            assertThatNoException()
                    .isThrownBy(() -> this.accessGuard.require(isLocalRunner()));
        }

    }

    private SecurityContext wrapDetails(Object details) {
        return new SecurityContextImpl(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return details;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        });
    }

     */
}