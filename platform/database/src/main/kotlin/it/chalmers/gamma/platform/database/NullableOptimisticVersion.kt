package it.chalmers.gamma.platform.database

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.or

fun Column<Int?>.matchesStoredVersion(expectedVersion: Int): Op<Boolean> =
    if (expectedVersion == 0) {
        isNull() or (this eq 0)
    } else {
        this eq expectedVersion
    }
