package it.chalmers.gamma

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.testing.RedisTestEnvironment
import it.chalmers.gamma.users.UserConflict
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@TestConfiguration(proxyBeanMethods = false)
class TestInfrastructure {
    @Bean
    fun errorMappingProbeController() = ErrorMappingProbeController()

    @Bean
    fun transactionStatementCounter() = TransactionStatementCounter()
}

class TransactionStatementCounter : StatementInterceptor {
    private val recording = AtomicBoolean(false)
    private val completedTransactionStatementCounts = ConcurrentLinkedQueue<Int>()

    override fun afterCommit(transaction: Transaction) {
        if (recording.get()) completedTransactionStatementCounts += transaction.statementCount
    }

    fun <T> record(block: () -> T): Pair<T, List<Int>> {
        completedTransactionStatementCounts.clear()
        recording.set(true)
        return try {
            val result = block()
            result to completedTransactionStatementCounts.toList()
        } finally {
            recording.set(false)
        }
    }
}

@RestController
class ErrorMappingProbeController {
    @GetMapping("/test/errors/access-denied")
    fun accessDenied(): Nothing = throw AccessDenied()

    @GetMapping("/test/errors/conflict")
    fun conflict(): Nothing = throw UserConflict("sensitive conflict detail")

    @GetMapping("/test/errors/invalid")
    fun invalid(): Nothing = throw IllegalArgumentException("sensitive invalid detail")

    @PostMapping("/test/errors/upload")
    fun upload(): Nothing = throw MaxUploadSizeExceededException(1)

    @GetMapping("/test/errors/unhandled")
    fun unhandled(): Nothing = error("sensitive unhandled detail")
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestInfrastructure::class)
open class SpringApplicationTest {
    @LocalServerPort
    protected var port: Int = 0

    protected fun browser(clientAddress: String = "127.0.0.1"): TestBrowser = TestBrowser(port, clientAddress)

    companion object {
        private val postgres = PostgresTestEnvironment()
        private val redis = RedisTestEnvironment()

        @JvmStatic
        @DynamicPropertySource
        fun applicationProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.port }
            registry.add("application.admin-setup") { "false" }
            registry.add("application.production") { "false" }
            registry.add("application.mocking") { "false" }
            registry.add("server.tomcat.remoteip.internal-proxies") { "127\\.0\\.0\\.1" }
        }
    }
}

data class TestResponse(
    val status: Int,
    val headers: Map<String, List<String>>,
    val body: String,
) {
    fun header(name: String): String? =
        headers.entries
            .firstOrNull { it.key.equals(name, ignoreCase = true) }
            ?.value
            ?.firstOrNull()
}

class TestBrowser(
    private val port: Int,
    private val clientAddress: String,
) {
    private val client =
        HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build()
    var sessionCookie: String? = null
        private set

    fun get(
        path: String,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse = request("GET", path, headers = headers)

    fun formMulti(
        method: String,
        path: String,
        fields: Map<String, List<String>>,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse =
        request(
            method,
            path,
            formBody = encodeForm(fields),
            headers = headers + ("Content-Type" to "application/x-www-form-urlencoded"),
        )

    fun form(
        method: String,
        path: String,
        fields: Map<String, String>,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse = formMulti(method, path, fields.mapValues { listOf(it.value) }, headers)

    fun json(
        method: String,
        path: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse =
        request(
            method,
            path,
            body,
            headers + ("Content-Type" to "application/json"),
        )

    fun request(
        method: String,
        path: String,
        formBody: String? = null,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse {
        val target = if (path.startsWith("http://") || path.startsWith("https://")) path else baseUrl + path
        val builder =
            HttpRequest
                .newBuilder(URI.create(target))
                .method(
                    method,
                    formBody?.let(HttpRequest.BodyPublishers::ofString) ?: HttpRequest.BodyPublishers.noBody(),
                )
        if (headers.keys.none { it.equals("X-Forwarded-For", ignoreCase = true) }) {
            builder.header("X-Forwarded-For", clientAddress)
        }
        sessionCookie?.let { builder.header("Cookie", it) }
        headers.forEach(builder::header)
        val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
        response
            .headers()
            .allValues("Set-Cookie")
            .map { it.substringBefore(';') }
            .firstOrNull { it.startsWith("SESSION=") }
            ?.let { sessionCookie = it }
        return TestResponse(response.statusCode(), response.headers().map(), response.body())
    }

    fun csrf(path: String = "/login"): Pair<TestResponse, String> {
        val response = get(path)
        return response to extractCsrf(response.body)
    }

    fun login(
        username: String = "mscott",
        password: String = "password1337",
    ): TestResponse {
        val (_, token) = csrf()
        return form(
            "POST",
            "/login",
            mapOf("username" to username, "password" to password, "_csrf" to token),
        )
    }

    val baseUrl: String
        get() = "http://127.0.0.1:$port"
}

internal fun extractCsrf(html: String): String =
    requireNotNull(Regex("""name=[\"']_csrf[\"'][^>]*value=[\"']([^\"']+)[\"']""").find(html)?.groupValues?.get(1)) {
        "Response did not contain a CSRF field"
    }

private val nextTestAddress = AtomicInteger()

internal fun uniqueAddress(): String {
    // Random selection from 256 addresses lets unrelated browsers share throttle state in a full run.
    val address = nextTestAddress.incrementAndGet()
    check(address in 1..0xFFFFFF) { "Test client address range exhausted" }
    return "10.${address ushr 16 and 255}.${address ushr 8 and 255}.${address and 255}"
}

private fun encodeForm(fields: Map<String, List<String>>): String =
    fields.entries
        .flatMap { (name, values) -> values.map { value -> urlEncode(name) + "=" + urlEncode(value) } }
        .joinToString("&")

private fun urlEncode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)
