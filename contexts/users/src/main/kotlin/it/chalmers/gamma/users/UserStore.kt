package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory

/**
 * The database-backed entry point for user data.
 *
 * Callers depend on this one store. Its module-internal persistence components each keep one
 * independently meaningful rule set readable; folding them together would create a 1,300-line
 * class without changing the public boundary.
 */
@Suppress("TooManyFunctions") // One explicit store owns every read and mutation for the user aggregate.
class UserStore(
    database: DatabaseFactory,
    passwordHasher: PasswordHasher,
) {
    private val queries = UserQueries(database)
    private val credentials = UserCredentials(database, passwordHasher)
    private val commands = UserCommands(database, passwordHasher, credentials)
    private val accessFlags = UserAccessFlags(database)
    private val avatarPointers = UserAvatarPointers(database)

    fun findUser(identifier: UserIdentifier): UserProfile? = queries.findUser(identifier)

    fun usersByIds(userIds: Set<UserId>): List<UserProfile> = queries.usersByIds(userIds)

    fun directoryUserPage(request: DirectoryUserPageRequest): DirectoryUserPage = queries.directoryUserPage(request)

    fun findDirectoryUser(userId: UserId): DirectoryUser? = queries.findDirectoryUser(userId)

    fun administrativeUser(
        administratorId: UserId,
        userId: UserId,
    ): AdministrativeUser? = queries.administrativeUser(administratorId, userId)

    fun administrativeUsers(administratorId: UserId): List<UserProfile> = queries.administrativeUsers(administratorId)

    fun apiUser(userId: UserId): ApiUserProfile? = queries.apiUser(userId)

    fun apiUsers(): List<ApiUserProfile> = queries.apiUsers()

    fun apiUsersByIds(userIds: Set<UserId>): List<ApiUserProfile> = queries.apiUsersByIds(userIds)

    fun userExists(cid: Cid): Boolean = queries.userExists(cid)

    fun isAdministrator(userId: UserId): Boolean = queries.isAdministrator(userId)

    fun isGdprTrained(userId: UserId): Boolean = queries.isGdprTrained(userId)

    fun sessionAccess(userId: UserId): SessionAccess? = queries.sessionAccess(userId)

    fun createUserAsAdministrator(
        administratorId: UserId,
        input: NewUser,
    ): UserId = commands.createUserAsAdministrator(administratorId, input)

    fun updateUser(profile: UserProfile) = commands.updateUser(profile)

    fun updateUserAsAdministrator(
        administratorId: UserId,
        profile: UserProfile,
    ) = commands.updateUserAsAdministrator(administratorId, profile)

    fun checkPassword(
        userId: UserId,
        password: PlainTextPassword,
    ): Boolean = commands.checkPassword(userId, password)

    fun changePassword(
        userId: UserId,
        currentPassword: PlainTextPassword,
        newPassword: PlainTextPassword,
    ): Boolean = commands.changePassword(userId, currentPassword, newPassword)

    fun deleteUser(userId: UserId): String? = commands.deleteUser(userId)

    fun listAccessFlags(
        administratorId: UserId,
        kind: UserAccessFlagKind,
    ): List<UserAccessFlag> = accessFlags.list(administratorId, kind)

    fun replaceAccessFlags(
        administratorId: UserId,
        kind: UserAccessFlagKind,
        selectedUserIds: Set<UserId>,
    ) = accessFlags.replace(administratorId, kind, selectedUserIds)

    internal fun prepareRegistration(input: NewUser): PreparedUserRegistration = credentials.prepareRegistration(input)

    internal fun createActivatedUser(
        registration: PreparedUserRegistration,
        claim: ActivationCodeClaim,
    ): UserId = credentials.createActivatedUser(registration, claim)

    internal fun preparePasswordChange(
        userId: UserId,
        password: PlainTextPassword,
    ): PreparedPasswordChange = credentials.preparePasswordChange(userId, password)

    internal fun persistClaimedPasswordChange(
        change: PreparedPasswordChange,
        claim: PasswordResetClaim,
    ) = credentials.persistClaimedPasswordChange(change, claim)

    fun existingConfiguration(): AdministratorBootstrapResult? = credentials.existingConfiguration()

    internal fun createAdministrator(registration: PreparedUserRegistration): AdministratorBootstrapResult =
        credentials.createAdministrator(registration)

    fun replaceAvatar(
        userId: UserId,
        operationId: UserAvatarOperationId,
        avatar: StoredUserAvatar,
    ): StoredUserAvatar? = avatarPointers.replaceAvatar(userId, operationId, avatar)

    fun currentAvatar(userId: UserId): StoredUserAvatar? = avatarPointers.currentAvatar(userId)

    fun currentAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
    ): StoredUserAvatar? = avatarPointers.currentAvatarAsAdministrator(administratorId, userId)

    fun clearAvatar(
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) = avatarPointers.clearAvatar(userId, expectedAvatar)

    fun clearAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) = avatarPointers.clearAvatarAsAdministrator(administratorId, userId, expectedAvatar)
}

internal data class PreparedUserRegistration(
    val cid: Cid,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val language: Language?,
    val email: Email,
    val passwordHash: PasswordHash,
) {
    override fun toString(): String = "PreparedUserRegistration(<redacted>)"
}

internal data class PreparedPasswordChange(
    val userId: UserId,
    val passwordHash: PasswordHash,
    val expectedVersion: Int,
) {
    init {
        require(expectedVersion >= 0) { "Expected user version must not be negative" }
    }

    override fun toString(): String = "PreparedPasswordChange(<redacted>)"
}
