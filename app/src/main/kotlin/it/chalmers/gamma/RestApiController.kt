package it.chalmers.gamma

import it.chalmers.gamma.api.AccountScaffoldApi
import it.chalmers.gamma.api.AllowListApi
import it.chalmers.gamma.api.ClientApi
import it.chalmers.gamma.api.InfoApi
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.views.AccountScaffoldSuperGroup
import it.chalmers.gamma.apiaccess.views.AllowListAddedResponse
import it.chalmers.gamma.apiaccess.views.AllowListRequest
import it.chalmers.gamma.apiaccess.views.ApiError
import it.chalmers.gamma.apiaccess.views.ApiUser
import it.chalmers.gamma.apiaccess.views.InfoBlobResponse
import it.chalmers.gamma.apiaccess.views.InfoUserResponse
import it.chalmers.gamma.oauth.views.ClientApiGroup
import it.chalmers.gamma.oauth.views.ClientApiMembership
import it.chalmers.gamma.oauth.views.ClientApiSuperGroup
import it.chalmers.gamma.oauth.views.ClientApiUser
import it.chalmers.gamma.users.UserId
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class RestApiController(
    private val information: InfoApi,
    private val accountScaffold: AccountScaffoldApi,
    private val allowList: AllowListApi,
    private val clientApi: ClientApi,
) {
    @GetMapping("/api/info/v1/blob")
    fun infoBlob(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.INFO) { apiKeyId ->
            // Empty settings are the normal bootstrap contract; the rich projection remains owned by the use case.
            information.blob(apiKeyId).map(InfoBlobResponse::from)
        }

    @GetMapping("/api/info/v1/users/{userId}")
    fun infoUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @PathVariable userId: String,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.INFO) { apiKeyId ->
            information.user(apiKeyId, UserId.parse(userId))?.let(InfoUserResponse::from)
                ?: ApiFailure(HttpStatus.NOT_FOUND, ApiError(404, "Not Found", "USER_NOT_FOUND_RESPONSE"))
        }

    @GetMapping("/api/account-scaffold/v1/supergroups")
    fun accountScaffoldSuperGroups(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.ACCOUNT_SCAFFOLD) { apiKeyId ->
            accountScaffold.superGroups(apiKeyId).map(AccountScaffoldSuperGroup::from)
        }

    @GetMapping("/api/account-scaffold/v1/users")
    fun accountScaffoldUsers(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.ACCOUNT_SCAFFOLD) { apiKeyId ->
            accountScaffold.users(apiKeyId).map(ApiUser::from)
        }

    @GetMapping("/api/allow-list/v1")
    fun allowList(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.ALLOW_LIST) {
            allowList.allowedCids()
        }

    @PostMapping("/api/allow-list/v1")
    fun addAllowList(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @RequestBody body: AllowListRequest,
    ): ResponseEntity<Any> {
        checkNotNull(authorization)
        val authentication = checkNotNull(SecurityContextHolder.getContext().authentication)
        val principal = authentication.principal as AuthenticatedApiKey
        if (principal.key.type != ApiKeyType.ALLOW_LIST) {
            return ResponseEntity.status(403).body(ApiError(403, "Forbidden", "FORBIDDEN"))
        }

        // Each CID owns its commit or rollback. An outer transaction would let a caught
        // item failure either retain rejected writes or roll back previously accepted items.
        val failures = allowList.allow(body.cids.orEmpty())
        return if (failures.isEmpty()) {
            ResponseEntity.ok(AllowListAddedResponse("ALLOW_LIST_ADDED_RESPONSE", 200))
        } else {
            ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(failures)
        }
    }

    @GetMapping("/api/client/v1/groups")
    fun clientGroups(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { clientApi.groups(it).map(ClientApiGroup::from) }

    @GetMapping("/api/client/v1/superGroups")
    fun clientSuperGroups(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { apiKeyId ->
            clientApi.superGroups(apiKeyId).map(ClientApiSuperGroup::from)
        }

    @GetMapping("/api/client/v1/users")
    fun clientUsers(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { clientApi.approvedUsers(it).map(ClientApiUser::from) }

    @GetMapping("/api/client/v1/users/{userId}")
    fun clientUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @PathVariable userId: String,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { apiKeyId ->
            val user = runCatching { UserId.parse(userId) }.getOrNull()?.let { clientApi.approvedUser(apiKeyId, it) }
            user?.let(ClientApiUser::from) ?: userNotFound()
        }

    @GetMapping("/api/client/v1/groups/for/{userId}")
    fun clientGroupsForUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @PathVariable userId: String,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { apiKeyId ->
            val id = runCatching { UserId.parse(userId) }.getOrNull()
            val memberships = id?.let { clientApi.membershipsForApprovedUser(apiKeyId, it) }
            memberships?.map { ClientApiMembership.from(it.group, it.post) } ?: userNotFound()
        }

    @GetMapping("/api/client/v1/authorities")
    fun clientAuthorities(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { apiKeyId ->
            clientApi.authorities(apiKeyId).map { it.name.value }
        }

    @GetMapping("/api/client/v1/authorities/for/{userId}")
    fun clientAuthoritiesForUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @PathVariable userId: String,
    ): ResponseEntity<Any> =
        withApiActor(authorization, ApiKeyType.CLIENT) { apiKeyId ->
            val id = runCatching { UserId.parse(userId) }.getOrNull() ?: return@withApiActor userNotFound()
            clientApi.authoritiesForUser(apiKeyId, id).map { it.value }
        }

    private fun withApiActor(
        authorization: String?,
        expectedType: ApiKeyType,
        block: (ApiKeyId) -> Any,
    ): ResponseEntity<Any> {
        // The API security chain authenticates this header before a controller is invoked.
        checkNotNull(authorization)
        val authentication = checkNotNull(SecurityContextHolder.getContext().authentication)
        val principal = authentication.principal as AuthenticatedApiKey
        if (principal.key.type != expectedType) {
            return ResponseEntity.status(403).body(ApiError(403, "Forbidden", "FORBIDDEN"))
        }
        val result = block(principal.key.id)
        return when (result) {
            is ApiFailure -> ResponseEntity.status(result.status).body(result.body)
            else -> ResponseEntity.ok(result)
        }
    }
}

private data class ApiFailure(
    val status: HttpStatus,
    val body: Any,
)

private fun userNotFound() =
    ApiFailure(HttpStatus.NOT_FOUND, ApiError(404, "Not Found", "User Not Found Or Unauthorized"))
