package it.chalmers.gamma.platform.database

import it.chalmers.gamma.platform.core.UnitOfWork

fun databaseUnitOfWork(database: DatabaseFactory): UnitOfWork = DatabaseUnitOfWork(database)

private class DatabaseUnitOfWork(
    private val database: DatabaseFactory,
) : UnitOfWork {
    override fun <Result> run(operation: () -> Result): Result = database.transaction { operation() }
}
