package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.database.DatabaseFactory

// Context tests exercise the credential phases together; the application tests
// exercise the administrative operation that owns authorization and composition.
internal fun RotateApiKey.rotateForTest(
    database: DatabaseFactory,
    id: ApiKeyId,
): RotatedApiKey {
    val prepared = prepare(id)
    val result = database.commitTransaction { replaceIn(this, prepared) }
    publishAfterCommit(prepared)
    return result
}
