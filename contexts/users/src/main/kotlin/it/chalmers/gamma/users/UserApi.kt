package it.chalmers.gamma.users

data class DirectoryUser(
    val id: UserId,
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val version: Int,
    val locked: Boolean,
) {
    override fun toString(): String = "DirectoryUser(<redacted>)"
}

data class DirectoryUserPageRequest(
    val query: String,
    val afterCid: Cid?,
    val scope: DirectoryUserScope,
) {
    override fun toString(): String =
        "DirectoryUserPageRequest(query=<redacted>, " +
            "afterCid=${if (afterCid == null) "<start>" else "<present>"}, scope=$scope)"
}

data class DirectoryUserScope(
    val userId: UserId,
    val access: DirectoryUserAccess,
) {
    companion object {
        fun administrator(userId: UserId) = DirectoryUserScope(userId, DirectoryUserAccess.ADMINISTRATOR)

        fun visibleToUser(userId: UserId) = DirectoryUserScope(userId, DirectoryUserAccess.VISIBLE_TO_USER)
    }
}

enum class DirectoryUserAccess {
    ADMINISTRATOR,
    VISIBLE_TO_USER,
}

data class AdministrativeUser(
    val profile: UserProfile,
    val gdprTrained: Boolean,
) {
    override fun toString(): String = "AdministrativeUser(<redacted>)"
}

class DirectoryUserPage(
    users: List<DirectoryUser>,
    val nextCid: Cid?,
) {
    val users: List<DirectoryUser> = users.toList()

    init {
        require(users.size <= MAXIMUM_DIRECTORY_PAGE_SIZE) {
            "A directory page may contain at most $MAXIMUM_DIRECTORY_PAGE_SIZE users"
        }
        require(nextCid == null || users.lastOrNull()?.cid == nextCid) {
            "The next directory cursor must identify the final user in the page"
        }
        require(users.zipWithNext().all { (first, second) -> first.cid.value < second.cid.value }) {
            "Directory users must be in strictly ascending CID order"
        }
    }

    override fun toString(): String =
        "DirectoryUserPage(users=${users.size}, nextCid=${if (nextCid == null) "<end>" else "<present>"})"
}

const val MAXIMUM_DIRECTORY_PAGE_SIZE = 200

data class UserDetails(
    val id: UserId,
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val version: Int,
    val administration: UserAdministrationDetails? = null,
) {
    override fun toString(): String = "UserDetails(<redacted>)"
}

data class UserAdministrationDetails(
    val email: Email,
    val locked: Boolean,
    val gdprTrained: Boolean,
) {
    override fun toString(): String = "UserAdministrationDetails(<redacted>)"
}

data class ApiUserProfile(
    val id: UserId,
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val email: Email,
    val locked: Boolean,
    val gdprTrained: Boolean,
) {
    override fun toString(): String = "ApiUserProfile(<redacted>)"
}

data class NewUser(
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val language: Language?,
    val email: Email,
    val password: PlainTextPassword,
) {
    override fun toString(): String = "NewUser(<redacted>)"
}

interface PasswordHasher {
    fun hash(password: PlainTextPassword): PasswordHash

    fun verify(
        password: PlainTextPassword,
        hash: PasswordHash,
    ): Boolean

    fun verifyAgainstDummy(password: PlainTextPassword): Boolean
}

class UserNotFound(
    message: String,
) : RuntimeException(message)

class UserConflict(
    message: String,
) : RuntimeException(message)
