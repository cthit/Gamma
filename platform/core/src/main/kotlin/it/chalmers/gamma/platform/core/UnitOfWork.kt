package it.chalmers.gamma.platform.core

/**
 * Runs one operation as one atomic unit.
 *
 * The operation can run zero times when the unit cannot begin, or more than once when an
 * implementation retries a failure according to its adapter policy. Callers must therefore make
 * the operation safe to retry. Normal return means the unit committed. Each failed attempt rolls
 * back that attempt's transactional effects; only the terminal failure escapes to the caller, and
 * an adapter may wrap it with transaction context. The returned value always comes from the attempt
 * that committed; values produced by rolled-back attempts never escape.
 */
interface UnitOfWork {
    fun <Result> run(operation: () -> Result): Result
}
