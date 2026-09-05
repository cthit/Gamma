package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId

// A caller's cached flag deliberately does not grant administrator authority to user operations.
internal fun UserProfile.profileActor(isAdministrator: Boolean = false) =
    Actor.User(ActorUserId(id.value), isAdministrator)

internal fun UserProfile.userUpdate() =
    UserUpdate(
        userId = id,
        expectedVersion = version,
        nick = nick,
        firstName = firstName,
        lastName = lastName,
        acceptanceYear = acceptanceYear,
        language = language,
        email = email,
    )

internal fun UserProfile.myProfileUpdate() = MyProfileUpdate(nick, firstName, lastName, language, email, version)
