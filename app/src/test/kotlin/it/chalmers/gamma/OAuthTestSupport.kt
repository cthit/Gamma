package it.chalmers.gamma

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import javax.sql.DataSource

internal data class OAuthTestClient(
    val uid: UUID,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)

internal data class AuthorizationCodeRequest(
    val code: String,
    val verifier: String?,
)

internal fun DataSource.createOAuthTestClient(
    port: Int,
    restrictedSuperGroupId: UUID? = null,
): OAuthTestClient {
    val uid = UUID.randomUUID()
    val clientId =
        uid
            .toString()
            .replace("-", "")
            .uppercase()
            .take(30)
    val redirectUri = "http://127.0.0.1:$port/oauth2/callback"
    connection.use { connection ->
        connection.autoCommit = false
        connection.createStatement().use { statement ->
            statement.execute(
                """
                INSERT INTO g_client (
                    client_uid, client_id, client_secret, redirect_uri, pretty_name,
                    created_at, description, official, created_by
                ) VALUES (
                    '$uid', '$clientId',
                    '{bcrypt}${'$'}2y${'$'}10${'$'}43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu',
                    '$redirectUri', 'Spring test client', NOW(), NULL, TRUE, NULL
                );
                INSERT INTO g_client_scope (client_uid, scope, created_at)
                VALUES ('$uid', 'PROFILE', NOW()), ('$uid', 'EMAIL', NOW());
                """.trimIndent(),
            )
            if (restrictedSuperGroupId != null) {
                statement.execute(
                    """
                    INSERT INTO g_client_restriction (created_at, restriction_id, client_uid)
                    VALUES (NOW(), '$uid', '$uid');
                    INSERT INTO g_client_restriction_super_group (created_at, super_group_id, restriction_id)
                    VALUES (NOW(), '$restrictedSuperGroupId', '$uid');
                    """.trimIndent(),
                )
            }
        }
        connection.commit()
    }
    return OAuthTestClient(uid, clientId, API_TEST_SECRET, redirectUri)
}

internal fun TestBrowser.authorize(
    client: OAuthTestClient,
    usePkce: Boolean,
    prompt: String? = null,
    maxAge: Int? = null,
): AuthorizationCodeRequest {
    val verifier = if (usePkce) "test-verifier-${UUID.randomUUID()}-abcdefghijklmnopqrstuvwxyz" else null
    val state = "state-${UUID.randomUUID()}"
    val nonce = "nonce-${UUID.randomUUID()}"
    val parameters =
        linkedMapOf(
            "response_type" to "code",
            "client_id" to client.clientId,
            "redirect_uri" to client.redirectUri,
            "scope" to "openid profile email",
            "state" to state,
            "nonce" to nonce,
        )
    if (verifier != null) {
        parameters["code_challenge"] = verifier.sha256Base64Url()
        parameters["code_challenge_method"] = "S256"
    }
    prompt?.let { parameters["prompt"] = it }
    maxAge?.let { parameters["max_age"] = it.toString() }

    var response = get("/oauth2/authorize?" + encodeQuery(parameters))
    if (response.status == 302 && response.header("Location")?.contains("/login") == true) {
        val loginPage = get(response.header("Location").toLocalPath())
        response =
            form(
                "POST",
                "/login",
                mapOf(
                    "username" to "mscott",
                    "password" to "password1337",
                    "_csrf" to extractCsrf(loginPage.body),
                ),
            )
    }
    response = followLocalRedirects(response, stopAt = client.redirectUri)
    if (response.body.contains("confirm-authorization")) {
        val form =
            requireNotNull(
                Regex(
                    """<form[^>]*id=[\"']confirm-authorization[\"'][^>]*>(.*?)</form>""",
                    setOf(RegexOption.DOT_MATCHES_ALL),
                ).find(response.body),
            ).groupValues[1]
        val fields =
            Regex("""<input[^>]*name=[\"']([^\"']+)[\"'][^>]*value=[\"']([^\"']*)[\"']""")
                .findAll(form)
                .groupBy({ it.groupValues[1] }, { it.groupValues[2] })
        response = formMulti("POST", "/oauth2/authorize", fields)
    }
    val location = requireNotNull(response.header("Location")) { "Authorization did not redirect: ${response.body}" }
    val callback = URI.create(location)
    val query = parseQuery(callback.rawQuery)
    check(query["state"] == state)
    return AuthorizationCodeRequest(
        requireNotNull(query["code"]) { "Authorization failed: $location" },
        verifier,
    )
}

internal fun OAuthTestClient.authorizationPath(
    prompt: String? = null,
    maxAge: Int? = null,
): String {
    val parameters =
        linkedMapOf(
            "response_type" to "code",
            "client_id" to clientId,
            "redirect_uri" to redirectUri,
            "scope" to "openid profile email",
            "state" to "state-${UUID.randomUUID()}",
            "nonce" to "nonce-${UUID.randomUUID()}",
        )
    prompt?.let { parameters["prompt"] = it }
    maxAge?.let { parameters["max_age"] = it.toString() }
    return "/oauth2/authorize?" + encodeQuery(parameters)
}

internal fun TestResponse.redirectParameters(): Map<String, String> =
    parseQuery(URI.create(requireNotNull(header("Location"))).rawQuery)

internal fun TestBrowser.exchangeCode(
    client: OAuthTestClient,
    authorization: AuthorizationCodeRequest,
    verifier: String? = authorization.verifier,
): TestResponse {
    val fields =
        linkedMapOf(
            "grant_type" to listOf("authorization_code"),
            "code" to listOf(authorization.code),
            "redirect_uri" to listOf(client.redirectUri),
        )
    verifier?.let { fields["code_verifier"] = listOf(it) }
    return formMulti(
        "POST",
        "/oauth2/token",
        fields,
        mapOf("Authorization" to client.basicAuthorization()),
    )
}

internal fun TestBrowser.followLocalRedirects(
    initial: TestResponse,
    stopAt: String? = null,
): TestResponse {
    var response = initial
    repeat(8) {
        val location = response.header("Location") ?: return response
        if (stopAt != null && location.startsWith(stopAt)) return response
        if (!location.startsWith("/") && !location.startsWith(baseUrl)) return response
        response = get(location.toLocalPath())
    }
    error("Too many local redirects")
}

internal fun OAuthTestClient.basicAuthorization(): String =
    "Basic " + Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

internal fun jsonString(
    json: String,
    name: String,
): String =
    requireNotNull(Regex("""[\"]${Regex.escape(name)}[\"]\s*:\s*[\"]([^\"]+)[\"]""").find(json)) {
        "JSON response did not contain $name: $json"
    }.groupValues[1]

internal fun jwtPart(
    jwt: String,
    index: Int,
): String = String(Base64.getUrlDecoder().decode(jwt.split('.')[index]), StandardCharsets.UTF_8)

private fun String.sha256Base64Url(): String =
    Base64
        .getUrlEncoder()
        .withoutPadding()
        .encodeToString(MessageDigest.getInstance("SHA-256").digest(toByteArray()))

private fun encodeQuery(parameters: Map<String, String>): String =
    parameters.entries.joinToString("&") { (name, value) ->
        java.net.URLEncoder.encode(name, StandardCharsets.UTF_8) +
            "=" +
            java.net.URLEncoder.encode(value, StandardCharsets.UTF_8)
    }

private fun parseQuery(query: String?): Map<String, String> =
    query
        .orEmpty()
        .split('&')
        .filter(String::isNotBlank)
        .associate { pair ->
            val (name, value) = pair.split('=', limit = 2).let { it[0] to it.getOrElse(1) { "" } }
            URLDecoder.decode(name, StandardCharsets.UTF_8) to URLDecoder.decode(value, StandardCharsets.UTF_8)
        }

private fun String?.toLocalPath(): String {
    val location = requireNotNull(this)
    if (location.startsWith('/')) return location
    val uri = URI.create(location)
    return uri.rawPath + uri.rawQuery?.let { "?$it" }.orEmpty()
}

private const val API_TEST_SECRET = "gamma-info-regression-token-000001"
