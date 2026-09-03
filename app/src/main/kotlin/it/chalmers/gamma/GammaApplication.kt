package it.chalmers.gamma

import it.chalmers.gamma.oauth.server.AuthorizationServerConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableConfigurationProperties(AppSettings::class)
@EnableRedisIndexedHttpSession(redisNamespace = "${'$'}{spring.session.redis.namespace:gamma:session}")
@Import(
    GammaBeans::class,
    MockDataBootstrap::class,
    ApplicationSecurityConfiguration::class,
    AuthorizationServerConfiguration::class,
    ApiKeyController::class,
    ApplicationErrorController::class,
    ApplicationErrorHandler::class,
    BrowserController::class,
    UserController::class,
    AccountActivationController::class,
    MediaController::class,
    OAuthClientController::class,
    OrganizationController::class,
    OrganizationTypeController::class,
    RestApiController::class,
    ThrottlingController::class,
    UserDeletionCascade::class,
)
class GammaApplication

// Spring's entry point accepts varargs; this copy happens once during process startup.
@Suppress("SpreadOperator")
fun main(arguments: Array<String>) {
    SpringApplication.run(GammaApplication::class.java, *arguments)
}
