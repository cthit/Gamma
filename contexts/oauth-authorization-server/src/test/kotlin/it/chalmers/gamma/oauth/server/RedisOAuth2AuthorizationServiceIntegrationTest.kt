package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisSettings
import it.chalmers.gamma.testing.RedisTestEnvironment
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RedisOAuth2AuthorizationServiceIntegrationTest {
    @Test
    fun `persists every supported lookup and explicit token property across instances`() {
        RedisTestEnvironment().use { environment ->
            val settings = RedisSettings(environment.host, environment.port)
            val now = Instant.parse("2026-08-23T08:00:00Z")
            val clock = MutableClock(now)
            val client = registeredClient()
            val clients = InMemoryRegisteredClientRepository(client)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val first =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(firstRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    val second =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(secondRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    val authorization = authorization(client, now)

                    first.save(authorization)

                    val restored = assertNotNull(second.findById(authorization.id))
                    assertEquals(authorization.principalName, restored.principalName)
                    assertEquals(authorization.authorizedScopes, restored.authorizedScopes)
                    val restoredRequest =
                        assertNotNull(
                            restored.getAttribute<OAuth2AuthorizationRequest>(
                                OAuth2AuthorizationRequest::class.java.name,
                            ),
                        )
                    assertEquals("challenge-value", restoredRequest.additionalParameters["code_challenge"])
                    assertEquals("S256", restoredRequest.additionalParameters["code_challenge_method"])
                    assertEquals("oidc-nonce", restoredRequest.additionalParameters["nonce"])

                    lookups(authorization).forEach { (value, type) ->
                        assertEquals(authorization.id, assertNotNull(second.findByToken(value, type)).id)
                        assertEquals(authorization.id, assertNotNull(second.findByToken(value, null)).id)
                    }
                    val accessToken = assertNotNull(restored.accessToken)
                    assertEquals(setOf("openid", "profile"), accessToken.token.scopes)
                    assertEquals("subject", assertNotNull(accessToken.claims)["sub"])
                    val idToken = assertNotNull(restored.getToken(OidcIdToken::class.java))
                    assertEquals("https://gamma.example", assertNotNull(idToken.token.claims["iss"]).toString())
                    assertTrue(assertNotNull(restored.getToken(OAuth2AuthorizationCode::class.java)).isInvalidated)
                }
            }
        }
    }

    @Test
    fun `updates remove stale indexes and removed revisions cannot be saved again`() {
        RedisTestEnvironment().use { environment ->
            val settings = RedisSettings(environment.host, environment.port)
            val now = Instant.parse("2026-08-23T08:00:00Z")
            val clock = MutableClock(now)
            val client = registeredClient()
            val clients = InMemoryRegisteredClientRepository(client)
            GammaRedis(settings).use { redis ->
                val service =
                    redisOAuth2AuthorizationService(
                        RedisOAuthAuthorizationStore(redis, clock),
                        clients,
                        clock,
                        currentClientId = { client.clientId },
                    )
                val original = authorization(client, now)
                service.save(original)
                val replacementCode = OAuth2AuthorizationCode("replacement-code", now, now.plusSeconds(300))
                val persisted = assertNotNull(service.findById(original.id))
                val updated = OAuth2Authorization.from(persisted).token(replacementCode).build()

                service.save(updated)
                assertNull(service.findByToken("authorization-code", OAuth2TokenType("code")))
                assertEquals(updated.id, service.findByToken("replacement-code", OAuth2TokenType("code"))?.id)

                service.remove(updated)
                assertNull(service.findById(updated.id))
                lookups(updated).forEach { (value, type) -> assertNull(service.findByToken(value, type)) }
                assertFailsWith<OAuthAuthorizationStorageFailure> { service.save(updated) }
            }
        }
    }

    @Test
    fun `concurrent save and remove never leave a resurrected record or index`() {
        RedisTestEnvironment().use { environment ->
            val settings = RedisSettings(environment.host, environment.port)
            val now = Instant.parse("2026-08-23T08:00:00Z")
            val clock = MutableClock(now)
            val client = registeredClient()
            val clients = InMemoryRegisteredClientRepository(client)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val first =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(firstRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    val second =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(secondRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    repeat(20) { iteration ->
                        val original =
                            authorization(
                                client,
                                now,
                                AuthorizationOptions(
                                    id = UUID.randomUUID().toString(),
                                    code = "race-code-$iteration",
                                ),
                            )
                        first.save(original)
                        val persisted = assertNotNull(first.findById(original.id))
                        val updated =
                            OAuth2Authorization
                                .from(persisted)
                                .token(
                                    OAuth2AuthorizationCode(
                                        "race-replacement-$iteration",
                                        now,
                                        now.plusSeconds(300),
                                    ),
                                ).build()
                        Executors.newFixedThreadPool(2).use { workers ->
                            listOf(
                                workers.submit<Result<Unit>> { runCatching { first.save(updated) } },
                                workers.submit<Result<Unit>> { runCatching { second.remove(original) } },
                            ).forEach { it.get() }
                        }
                        assertNull(first.findById(original.id))
                        assertNull(first.findByToken("race-code-$iteration", OAuth2TokenType("code")))
                        assertNull(first.findByToken("race-replacement-$iteration", OAuth2TokenType("code")))
                    }
                }
            }
        }
    }

    @Test
    fun `a stale authorization cannot overwrite a newer token family`() {
        RedisTestEnvironment().use { environment ->
            val now = Instant.parse("2026-08-23T08:00:00Z")
            val clock = MutableClock(now)
            val client = registeredClient()
            val clients = InMemoryRegisteredClientRepository(client)
            GammaRedis(RedisSettings(environment.host, environment.port)).use { firstRedis ->
                GammaRedis(RedisSettings(environment.host, environment.port)).use { secondRedis ->
                    val first =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(firstRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    val second =
                        redisOAuth2AuthorizationService(
                            RedisOAuthAuthorizationStore(secondRedis, clock),
                            clients,
                            clock,
                            currentClientId = { client.clientId },
                        )
                    val original = authorization(client, now, AuthorizationOptions(onlyCode = true))
                    first.save(original)
                    val firstCaller = assertNotNull(first.findById(original.id))
                    val staleCaller = assertNotNull(second.findById(original.id))
                    val winner =
                        OAuth2Authorization
                            .from(firstCaller)
                            .token(
                                OAuth2AccessToken(
                                    OAuth2AccessToken.TokenType.BEARER,
                                    "winner",
                                    now,
                                    now.plusSeconds(300),
                                ),
                            ).build()
                    val loser =
                        OAuth2Authorization
                            .from(staleCaller)
                            .token(
                                OAuth2AccessToken(
                                    OAuth2AccessToken.TokenType.BEARER,
                                    "loser",
                                    now,
                                    now.plusSeconds(300),
                                ),
                            ).build()

                    first.save(winner)
                    assertFailsWith<OAuthAuthorizationConflict> { second.save(loser) }

                    assertNotNull(first.findByToken("winner", OAuth2TokenType("access_token")))
                    assertNull(first.findByToken("loser", OAuth2TokenType("access_token")))
                }
            }
        }
    }
}
