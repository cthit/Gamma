@file:Suppress("ktlint:standard:function-naming")

package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory

internal fun UserStoreForQueries(database: DatabaseFactory): UserStore =
    UserStore(database, AlwaysMatchingPasswordHasher)

internal fun ExposedUserAccessFlags(database: DatabaseFactory): UserStore =
    UserStore(database, AlwaysMatchingPasswordHasher)

internal fun ExposedUserAvatarChanges(database: DatabaseFactory): UserStore =
    UserStore(database, AlwaysMatchingPasswordHasher)

internal data class TestUserDatabaseAccess(
    val commands: UserStore,
    val lifecycleCredentials: UserStore,
    val administratorBootstrap: UserStore,
)

internal fun createUserDatabaseAccess(
    database: DatabaseFactory,
    passwordHasher: PasswordHasher,
): TestUserDatabaseAccess {
    val users = UserStore(database, passwordHasher)
    return TestUserDatabaseAccess(users, users, users)
}

internal data class UserPersistenceTestAdapters(
    val commands: UserStore,
    val commandStore: UserStore,
    val lifecycle: UserStore,
)

internal fun identityPersistenceTestAdapters(
    database: DatabaseFactory,
    passwordHasher: PasswordHasher,
): UserPersistenceTestAdapters {
    val users = UserStore(database, passwordHasher)
    return UserPersistenceTestAdapters(users, users, users)
}

internal fun UserStore.createActivatedTestUser(
    database: DatabaseFactory,
    user: NewUser,
): UserId {
    val activationCodes = ActivationCodes(database)
    activationCodes.allow(user.cid)
    val claim = checkNotNull(activationCodes.claim(activationCodes.create(user.cid)))
    return createActivatedUser(prepareRegistration(user), claim)
}
