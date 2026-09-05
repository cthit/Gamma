package it.chalmers.gamma

import it.chalmers.gamma.api.AccountScaffoldApi
import it.chalmers.gamma.api.AllowListApi
import it.chalmers.gamma.api.ClientApi
import it.chalmers.gamma.api.InfoApi
import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.apiaccess.ReplaceApiKeySettings
import it.chalmers.gamma.apiaccess.RotateApiKey
import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.DeleteClient
import it.chalmers.gamma.oauth.DeleteOwnedOAuthClients
import it.chalmers.gamma.oauth.OAuthClaimDecisions
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.ReadOAuthClientDetails
import it.chalmers.gamma.oauth.ReadOAuthClientLists
import it.chalmers.gamma.oauth.RotateClientSecret
import it.chalmers.gamma.oauth.server.OAuthIssuer
import it.chalmers.gamma.oauth.server.RedisOAuthAuthorizationStore
import it.chalmers.gamma.organization.ChangeMyPostNames
import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.CreatePost
import it.chalmers.gamma.organization.CreateSuperGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.DeletePost
import it.chalmers.gamma.organization.DeleteSuperGroup
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.ReadGroupPages
import it.chalmers.gamma.organization.ReorderPosts
import it.chalmers.gamma.organization.SuperGroupTypes
import it.chalmers.gamma.organization.UpdateGroup
import it.chalmers.gamma.organization.UpdatePost
import it.chalmers.gamma.organization.UpdateSuperGroup
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.notifications.DiscardingOutboundMail
import it.chalmers.gamma.platform.notifications.GotifyOutboundMail
import it.chalmers.gamma.platform.notifications.OutboundMail
import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.throttling.RedisThrottling
import it.chalmers.gamma.throttling.ThrottlingAdministration
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.AdministratorAccess
import it.chalmers.gamma.users.AdministratorBootstrapResult
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.ChangeMyPassword
import it.chalmers.gamma.users.CreatePasswordReset
import it.chalmers.gamma.users.CreateUser
import it.chalmers.gamma.users.GotifyUserMail
import it.chalmers.gamma.users.PasswordHasher
import it.chalmers.gamma.users.PasswordResets
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegisterUser
import it.chalmers.gamma.users.RequestActivation
import it.chalmers.gamma.users.RequestPasswordReset
import it.chalmers.gamma.users.ResetPassword
import it.chalmers.gamma.users.UpdateMyEmail
import it.chalmers.gamma.users.UpdateMyProfile
import it.chalmers.gamma.users.UpdateUser
import it.chalmers.gamma.users.UserAccessFlags
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserAuthentication
import it.chalmers.gamma.users.UserAvatars
import it.chalmers.gamma.users.UserBootstrap
import it.chalmers.gamma.users.UserDeletion
import it.chalmers.gamma.users.UserMail
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.session.config.SessionRepositoryCustomizer
import org.springframework.session.data.redis.RedisIndexedSessionRepository
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer
import java.security.SecureRandom
import java.time.Duration
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
@Suppress("TooManyFunctions") // The complete composition root is intentionally visible in one place.
internal class GammaBeans {
    @Bean
    fun indexedSessionRepositoryCustomizer(
        @Value("${'$'}{spring.session.timeout:43200s}") timeout: Duration,
    ): SessionRepositoryCustomizer<RedisIndexedSessionRepository> =
        SessionRepositoryCustomizer { repository -> repository.setDefaultMaxInactiveInterval(timeout) }

    @Bean
    @DependsOn("flywayInitializer")
    internal fun databaseFactory(
        dataSource: DataSource,
        statementInterceptors: List<StatementInterceptor>,
    ): DatabaseFactory = DatabaseFactory(dataSource, statementInterceptors)

    @Bean
    fun redisLifecycle(redis: GammaRedis) = RedisLifecycle(redis)

    @Bean
    fun userQueries(database: DatabaseFactory): UserQueries = UserQueries(database)

    @Bean
    fun mediaStore(settings: AppSettings): MediaStore = LocalMediaStore(settings.files.path)

    @Bean
    fun userAvatars(
        database: DatabaseFactory,
        media: MediaStore,
    ) = UserAvatars(database, media)

    @Bean
    fun passwordHasher(): PasswordHasher = BcryptPasswordHasher()

    @Bean
    fun userDeletion(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = UserDeletion(database, passwordHasher)

    @Bean
    fun ownedClientDeletion(database: DatabaseFactory) = DeleteOwnedOAuthClients(database)

    @Bean
    fun ownedApiKeyDeletion(database: DatabaseFactory) = DeleteOwnedApiKeys(database)

    @Bean
    fun userAuthentication(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ): UserAuthentication = UserAuthentication(database, passwordHasher)

    @Bean
    fun userBootstrap(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = UserBootstrap(database, passwordHasher)

    @Bean(destroyMethod = "close")
    fun gammaRedis(connectionFactory: LettuceConnectionFactory): GammaRedis = GammaRedis(connectionFactory)

    @Bean
    fun organizationQueries(database: DatabaseFactory): OrganizationQueries = OrganizationQueries(database)

    @Bean
    fun readGroupPages(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        organizations: OrganizationQueries,
        users: UserQueries,
    ) = ReadGroupPages(database, accounts, organizations, users)

    @Bean
    fun groupImages(
        database: DatabaseFactory,
        media: MediaStore,
    ) = GroupImages(database, media)

    @Bean
    fun oauthClients(database: DatabaseFactory): OAuthClientQueries = OAuthClientQueries(database)

    @Bean
    fun oauthProtocolClients(database: DatabaseFactory): OAuthProtocolClients = OAuthProtocolClients(database)

    @Bean
    fun clientApprovals(database: DatabaseFactory) = ClientApprovals(database)

    @Bean
    fun oauthClaimDecisions(users: UserQueries): OAuthClaimDecisions = OAuthClaimDecisions(users)

    @Bean
    fun oauthAuthorizationStore(redis: GammaRedis): RedisOAuthAuthorizationStore = RedisOAuthAuthorizationStore(redis)

    @Bean
    fun oauthIssuer(settings: AppSettings): OAuthIssuer = OAuthIssuer(settings.publicBaseUrl)

    @Bean
    fun outboundMail(settings: AppSettings): OutboundMail =
        if (settings.gotify.baseUrl.isBlank() || settings.gotify.apiKey.isBlank()) {
            DiscardingOutboundMail
        } else {
            GotifyOutboundMail(settings.gotify.baseUrl, settings.gotify.apiKey, settings.gotify.from)
        }

    @Bean
    fun userMail(
        outboundMail: OutboundMail,
        settings: AppSettings,
    ): UserMail = GotifyUserMail(outboundMail, settings.publicBaseUrl)

    @Bean
    internal fun administratorBootstrapRunner(
        settings: AppSettings,
        bootstrap: UserBootstrap,
    ): ApplicationRunner =
        ApplicationRunner {
            if (!settings.adminSetup) return@ApplicationRunner
            val password = if (settings.production) generatedSpringBootstrapPassword() else "password1337"
            val result = bootstrap.ensureAdministrator(PlainTextPassword(password))
            if (result == AdministratorBootstrapResult.CREATED) {
                springBootstrapLogger.info("Admin user created -> cid:admin,password:{}", password)
            }
        }

    @Bean
    fun cookieSerializer(): CookieSerializer =
        DefaultCookieSerializer().apply {
            setCookieName("SESSION")
            setCookiePath("/")
            setUseHttpOnlyCookie(true)
            setUseSecureCookie(true)
            setSameSite("Lax")
        }

    @Bean
    fun postCreation(database: DatabaseFactory) = CreatePost(database)

    @Bean
    fun postUpdates(database: DatabaseFactory) = UpdatePost(database)

    @Bean
    fun postDeletion(database: DatabaseFactory) = DeletePost(database)

    @Bean
    fun postOrdering(database: DatabaseFactory) = ReorderPosts(database)

    @Bean
    fun groupCreation(database: DatabaseFactory) = CreateGroup(database)

    @Bean
    fun groupUpdates(database: DatabaseFactory) = UpdateGroup(database)

    @Bean
    fun groupDeletion(database: DatabaseFactory) = DeleteGroup(database)

    @Bean
    fun personalPostNames(database: DatabaseFactory) = ChangeMyPostNames(database)

    @Bean
    fun superGroupTypes(database: DatabaseFactory) = SuperGroupTypes(database)

    @Bean
    fun superGroupCreation(database: DatabaseFactory) = CreateSuperGroup(database)

    @Bean
    fun superGroupUpdates(database: DatabaseFactory) = UpdateSuperGroup(database)

    @Bean
    fun superGroupDeletion(database: DatabaseFactory) = DeleteSuperGroup(database)

    @Bean
    fun apiAccess(database: DatabaseFactory) = ApiKeyQueries(database)

    @Bean
    fun administratorAccess(database: DatabaseFactory) = AdministratorAccess(database)

    @Bean
    fun apiKeyCreation(database: DatabaseFactory) = CreateApiKey(database)

    @Bean
    fun administrativeApiKeyCreation(
        database: DatabaseFactory,
        administrators: AdministratorAccess,
        creation: CreateApiKey,
    ) = CreateAdministrativeApiKey(database, administrators, creation)

    @Bean
    fun apiKeyRotation(database: DatabaseFactory) = RotateApiKey(database)

    @Bean
    fun apiKeyDeletion(database: DatabaseFactory) = DeleteApiKey(database)

    @Bean
    fun administrativeApiKeyRotation(
        database: DatabaseFactory,
        administrators: AdministratorAccess,
        rotation: RotateApiKey,
    ) = RotateAdministrativeApiKey(database, administrators, rotation)

    @Bean
    fun administrativeApiKeyDeletion(
        database: DatabaseFactory,
        administrators: AdministratorAccess,
        deletion: DeleteApiKey,
    ) = DeleteAdministrativeApiKey(database, administrators, deletion)

    @Bean
    fun apiKeySettingsUpdates(
        database: DatabaseFactory,
        administrators: AdministratorAccess,
    ) = UpdateApiKeySettings(database, administrators, ReplaceApiKeySettings(database))

    @Bean
    fun readAdministrativeApiKeys(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        keys: ApiKeyQueries,
    ) = ReadAdministrativeApiKeys(database, accounts, keys)

    @Bean
    fun activationCodes(database: DatabaseFactory) = ActivationCodes(database)

    @Bean
    fun passwordResets(database: DatabaseFactory) = PasswordResets(database)

    @Bean
    fun activationCodeAdministration(database: DatabaseFactory) = ActivationCodeAdministration(database)

    @Bean
    fun createPasswordReset(database: DatabaseFactory) = CreatePasswordReset(database)

    @Bean
    fun userAccessFlags(database: DatabaseFactory) = UserAccessFlags(database)

    @Bean
    fun updateUser(database: DatabaseFactory) = UpdateUser(database)

    @Bean
    fun createUser(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = CreateUser(database, passwordHasher)

    @Bean
    fun registerUser(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = RegisterUser(database, passwordHasher)

    @Bean
    fun resetPassword(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = ResetPassword(database, passwordHasher)

    @Bean
    fun changeMyPassword(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ) = ChangeMyPassword(database, passwordHasher)

    @Bean
    fun updateMyProfile(database: DatabaseFactory) = UpdateMyProfile(database)

    @Bean
    fun updateMyEmail(database: DatabaseFactory) = UpdateMyEmail(database)

    @Bean
    fun requestActivation(
        database: DatabaseFactory,
        throttling: RedisThrottling,
        mail: UserMail,
    ) = RequestActivation(database, throttling, mail)

    @Bean
    fun requestPasswordReset(
        database: DatabaseFactory,
        throttling: RedisThrottling,
        mail: UserMail,
    ) = RequestPasswordReset(database, throttling, mail)

    @Bean
    fun userAccountAccess(database: DatabaseFactory) = UserAccountAccess(database)

    @Bean
    fun clientCreation(database: DatabaseFactory) = CreateClient(database)

    @Bean
    fun oauthClientCreation(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        clients: CreateClient,
        apiKeys: CreateApiKey,
    ) = CreateOAuthClient(database, accounts, clients, apiKeys)

    @Bean
    fun oauthClientDeletion(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        apiKeys: DeleteOwnedApiKeys,
    ) = DeleteOAuthClient(database, accounts, DeleteClient(database), apiKeys)

    @Bean
    fun oauthClientSecretReset(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
    ) = ResetOAuthClientSecret(database, accounts, RotateClientSecret(database))

    @Bean
    fun clientAuthorities(database: DatabaseFactory) = ClientAuthorities(database)

    @Bean
    fun createClientAuthority(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        authorities: ClientAuthorities,
    ) = CreateOAuthClientAuthority(database, accounts, authorities)

    @Bean
    fun deleteClientAuthority(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        authorities: ClientAuthorities,
    ) = DeleteOAuthClientAuthority(database, accounts, authorities)

    @Bean
    fun readOAuthClientDetails(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        clients: OAuthClientQueries,
    ) = ReadOAuthClientDetails(database, accounts, clients)

    @Bean
    fun readOAuthClientLists(
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        clients: OAuthClientQueries,
        users: UserQueries,
    ) = ReadOAuthClientLists(database, accounts, clients, users)

    @Bean
    fun apiCredentialAuthenticator(database: DatabaseFactory) = ApiCredentialAuthenticator(database)

    @Bean
    fun redisThrottling(redis: GammaRedis) = RedisThrottling(redis)

    @Bean
    fun throttlingAdministration(throttling: RedisThrottling) = ThrottlingAdministration(throttling)

    @Bean
    fun apiInformation(
        database: DatabaseFactory,
        apiKeys: ApiKeyQueries,
        users: UserQueries,
        organizations: OrganizationQueries,
    ) = InfoApi(
        database,
        apiKeys,
        users,
        organizations,
    )

    @Bean
    fun accountScaffoldApi(
        database: DatabaseFactory,
        apiKeys: ApiKeyQueries,
        users: UserQueries,
        organizations: OrganizationQueries,
    ) = AccountScaffoldApi(database, apiKeys, users, organizations)

    @Bean
    fun allowListApi(activationCodes: ActivationCodes) = AllowListApi(activationCodes)

    @Bean
    fun oauthClientApi(
        database: DatabaseFactory,
        clients: OAuthClientQueries,
        users: UserQueries,
        organizations: OrganizationQueries,
    ) = ClientApi(
        database,
        clients,
        users,
        organizations,
    )
}

private val springBootstrapLogger = LoggerFactory.getLogger("it.chalmers.gamma.Bootstrap")

private fun generatedSpringBootstrapPassword(): String =
    CharArray(72) {
        SPRING_BOOTSTRAP_PASSWORD_CHARACTERS[springBootstrapRandom.nextInt(SPRING_BOOTSTRAP_PASSWORD_CHARACTERS.length)]
    }.concatToString()

private val springBootstrapRandom = SecureRandom()
private const val SPRING_BOOTSTRAP_PASSWORD_CHARACTERS =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"
