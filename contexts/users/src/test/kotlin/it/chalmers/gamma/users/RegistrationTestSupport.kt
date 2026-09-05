package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory

internal fun RegisterUser.createActivatedTestUser(
    database: DatabaseFactory,
    user: NewUser,
): UserId {
    val activationCodes = ActivationCodes(database)
    activationCodes.allow(user.cid)
    val token = database.seedActivationForTest(user.cid)
    return register(Actor.Anonymous, user.registration(token))
}

internal fun NewUser.registration(token: RegistrationToken) =
    UserRegistration(
        token,
        nick,
        firstName,
        lastName,
        acceptanceYear,
        language,
        email,
        password,
        password.value,
        true,
    )
