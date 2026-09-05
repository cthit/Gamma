package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.util.UUID

internal val deletionTestAdministrator =
    Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), false)
