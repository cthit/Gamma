package it.chalmers.gamma

import it.chalmers.gamma.api.AccountScaffoldApi
import it.chalmers.gamma.api.AllowListApi
import it.chalmers.gamma.api.ClientApi
import it.chalmers.gamma.api.InfoApi
import it.chalmers.gamma.apiaccess.ApiAccessAdministration
import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyStore
import it.chalmers.gamma.apiaccess.OAuthClientCredentials
import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.oauth.OAuthClaimDecisions
import it.chalmers.gamma.oauth.OAuthClientAdministration
import it.chalmers.gamma.oauth.OAuthClientStore
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.OAuthProtocolConsents
import it.chalmers.gamma.oauth.server.OAuthIssuer
import it.chalmers.gamma.oauth.server.RedisOAuthAuthorizationStore
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.OrganizationAdministration
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.databaseUnitOfWork
import it.chalmers.gamma.platform.notifications.DiscardingOutboundMail
import it.chalmers.gamma.platform.notifications.GotifyOutboundMail
import it.chalmers.gamma.platform.notifications.OutboundMail
import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.throttling.RedisThrottling
import it.chalmers.gamma.throttling.ThrottlingAdministration
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.AdministratorBootstrapResult
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.GotifyUserMail
import it.chalmers.gamma.users.MyAccount
import it.chalmers.gamma.users.PasswordHasher
import it.chalmers.gamma.users.PasswordResetAdministration
import it.chalmers.gamma.users.PasswordResets
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserAccessAdministration
import it.chalmers.gamma.users.UserAdministration
import it.chalmers.gamma.users.UserAuthentication
import it.chalmers.gamma.users.UserAvatars
import it.chalmers.gamma.users.UserBootstrap
import it.chalmers.gamma.users.UserLifecycle
import it.chalmers.gamma.users.UserLifecycleThrottling
import it.chalmers.gamma.users.UserMail
import it.chalmers.gamma.users.UserStore
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
    fun userStore(
        database: DatabaseFactory,
        passwordHasher: PasswordHasher,
    ): UserStore = UserStore(database, passwordHasher)

    @Bean
    fun mediaStore(settings: AppSettings): MediaStore = LocalMediaStore(settings.files.path)

    @Bean
    fun userAvatars(
        users: UserStore,
        media: MediaStore,
    ) = UserAvatars(users, media)

    @Bean
    fun passwordHasher(): PasswordHasher = BcryptPasswordHasher()

    @Bean
    fun userAuthentication(users: UserStore): UserAuthentication = UserAuthentication(users)

    @Bean
    fun userBootstrap(users: UserStore): UserBootstrap = UserBootstrap(users)

    @Bean(destroyMethod = "close")
    fun gammaRedis(connectionFactory: LettuceConnectionFactory): GammaRedis = GammaRedis(connectionFactory)

    @Bean
    fun organizationQueries(database: DatabaseFactory): OrganizationStore = OrganizationStore(database)

    @Bean
    fun groupImages(
        organizations: OrganizationStore,
        media: MediaStore,
    ) = GroupImages(organizations, media)

    @Bean
    fun oauthClients(database: DatabaseFactory): OAuthClientStore = OAuthClientStore(database)

    @Bean
    fun oauthProtocolClients(clients: OAuthClientStore): OAuthProtocolClients = OAuthProtocolClients(clients)

    @Bean
    fun oauthProtocolConsents(clients: OAuthClientStore): OAuthProtocolConsents = OAuthProtocolConsents(clients)

    @Bean
    fun oauthClaimDecisions(users: UserStore): OAuthClaimDecisions = OAuthClaimDecisions(users)

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
    fun organizationAdministration(
        organizations: OrganizationStore,
        database: DatabaseFactory,
    ) = OrganizationAdministration(organizations, databaseUnitOfWork(database))

    @Bean
    fun apiAccess(database: DatabaseFactory) = ApiKeyStore(database)

    @Bean
    fun apiAccessAdministration(apiAccess: ApiKeyStore) = ApiAccessAdministration(apiAccess)

    @Bean
    fun activationCodes(database: DatabaseFactory) = ActivationCodes(database)

    @Bean
    fun passwordResets(database: DatabaseFactory) = PasswordResets(database)

    @Bean
    fun activationCodeAdministration(activationCodes: ActivationCodes) = ActivationCodeAdministration(activationCodes)

    @Bean
    fun passwordResetAdministration(passwordResets: PasswordResets) = PasswordResetAdministration(passwordResets)

    @Bean
    fun userAccessAdministration(users: UserStore) = UserAccessAdministration(users)

    @Bean
    fun myAccount(
        users: UserStore,
        deletion: UserDeletionCascade,
    ) = MyAccount(users, deletion)

    @Bean
    fun userAdministration(
        users: UserStore,
        deletion: UserDeletionCascade,
    ) = UserAdministration(users, deletion)

    @Bean
    fun userAdministrationWeb(
        userStore: UserStore,
        users: UserAdministration,
        passwordResets: PasswordResetAdministration,
    ) = UserAdministrationWeb(userStore, users, passwordResets)

    @Bean
    fun myAccountWeb(
        myAccount: MyAccount,
        avatars: UserAvatars,
    ) = MyAccountWeb(myAccount, avatars)

    @Bean
    fun userLifecycle(
        users: UserStore,
        activationCodes: ActivationCodes,
        passwordResets: PasswordResets,
        throttling: RedisThrottling,
        mail: UserMail,
    ) = UserLifecycle(users, activationCodes, passwordResets, UserLifecycleThrottling(throttling), mail)

    @Bean
    fun oauthClientAdministration(
        clients: OAuthClientStore,
        apiAccess: ApiKeyStore,
    ) = OAuthClientAdministration(clients, OAuthClientCredentials(apiAccess))

    @Bean
    fun apiCredentialAuthenticator(apiAccess: ApiKeyStore) = ApiCredentialAuthenticator(apiAccess)

    @Bean
    fun redisThrottling(redis: GammaRedis) = RedisThrottling(redis)

    @Bean
    fun throttlingAdministration(throttling: RedisThrottling) = ThrottlingAdministration(throttling)

    @Bean
    fun apiInformation(
        apiKeys: ApiKeyStore,
        users: UserStore,
        organizations: OrganizationStore,
    ) = InfoApi(
        apiKeys,
        users,
        organizations,
    )

    @Bean
    fun accountScaffoldApi(
        apiKeys: ApiKeyStore,
        users: UserStore,
        organizations: OrganizationStore,
    ) = AccountScaffoldApi(apiKeys, users, organizations)

    @Bean
    fun allowListApi(activationCodes: ActivationCodes) = AllowListApi(activationCodes)

    @Bean
    fun oauthClientApi(
        clients: OAuthClientStore,
        users: UserStore,
        organizations: OrganizationStore,
    ) = ClientApi(
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
