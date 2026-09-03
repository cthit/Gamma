package it.chalmers.gamma.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

// Each recording resource transfers ownership to the lifecycle function under test.
@Suppress("MissingUseCall")
class OwnedResourcesTest {
    @Test
    fun `container start failure closes the container`() {
        val events = mutableListOf<String>()
        val failure = IllegalStateException("start failed")
        val container = RecordingResource("container", events)

        val thrown =
            assertFailsWith<IllegalStateException> {
                OwnedResources.acquire(
                    container,
                    {
                        events += "container.start"
                        throw failure
                    },
                    { error("pool must not be created") },
                    { error("initializer must not run") },
                )
            }

        assertSame(failure, thrown)
        assertEquals(listOf("container.start", "container.close"), events)
    }

    @Test
    fun `pool creation failure closes the container`() {
        val events = mutableListOf<String>()
        val failure = IllegalStateException("pool failed")
        val container = RecordingResource("container", events)

        val thrown =
            assertFailsWith<IllegalStateException> {
                OwnedResources.acquire(
                    container,
                    { events += "container.start" },
                    {
                        events += "pool.create"
                        throw failure
                    },
                    { error("initializer must not run") },
                )
            }

        assertSame(failure, thrown)
        assertEquals(listOf("container.start", "pool.create", "container.close"), events)
    }

    @Test
    fun `initializer failure closes the pool before the container`() {
        val events = mutableListOf<String>()
        val failure = IllegalStateException("initialize failed")
        val container = RecordingResource("container", events)
        val pool = RecordingResource("pool", events)

        val thrown =
            assertFailsWith<IllegalStateException> {
                OwnedResources.acquire(
                    container,
                    { events += "container.start" },
                    {
                        events += "pool.create"
                        pool
                    },
                    {
                        events += "database.initialize"
                        throw failure
                    },
                )
            }

        assertSame(failure, thrown)
        assertEquals(
            listOf("container.start", "pool.create", "database.initialize", "pool.close", "container.close"),
            events,
        )
    }

    @Test
    fun `successful acquisition retains both resources`() {
        val events = mutableListOf<String>()
        val container = RecordingResource("container", events)
        val pool = RecordingResource("pool", events)

        val acquiredPool =
            OwnedResources.acquire(
                container,
                { events += "container.start" },
                {
                    events += "pool.create"
                    pool
                },
                { events += "database.initialize" },
            )

        assertSame(pool, acquiredPool)
        assertEquals(listOf("container.start", "pool.create", "database.initialize"), events)
    }

    @Test
    fun `startup failure keeps cleanup failures suppressed in close order`() {
        val events = mutableListOf<String>()
        val startupFailure = AssertionError("startup failed")
        val poolFailure = IllegalStateException("pool close failed")
        val containerFailure = IllegalArgumentException("container close failed")
        val container = RecordingResource("container", events, containerFailure)
        val pool = RecordingResource("pool", events, poolFailure)

        val thrown =
            assertFailsWith<AssertionError> {
                OwnedResources.acquire(
                    container,
                    { events += "container.start" },
                    { pool },
                    { throw startupFailure },
                )
            }

        assertSame(startupFailure, thrown)
        assertEquals(listOf(poolFailure, containerFailure), thrown.suppressed.toList())
        assertEquals(listOf("container.start", "pool.close", "container.close"), events)
    }

    @Test
    fun `close keeps the pool failure and suppresses the container failure`() {
        val events = mutableListOf<String>()
        val poolFailure = IllegalStateException("pool close failed")
        val containerFailure = IllegalArgumentException("container close failed")
        val pool = RecordingResource("pool", events, poolFailure)
        val container = RecordingResource("container", events, containerFailure)

        val thrown = assertFailsWith<IllegalStateException> { OwnedResources.close(pool, container) }

        assertSame(poolFailure, thrown)
        assertEquals(listOf(containerFailure), thrown.suppressed.toList())
        assertEquals(listOf("pool.close", "container.close"), events)
    }

    @Test
    fun `cleanup avoids suppressing a failure onto itself`() {
        val events = mutableListOf<String>()
        val failure = IllegalStateException("same failure")
        val container = RecordingResource("container", events, failure)

        val thrown =
            assertFailsWith<IllegalStateException> {
                OwnedResources.acquire(
                    container,
                    { throw failure },
                    { error("pool must not be created") },
                    { error("initializer must not run") },
                )
            }

        assertSame(failure, thrown)
        assertEquals(emptyList(), thrown.suppressed.toList())
        assertEquals(listOf("container.close"), events)
    }
}

private class RecordingResource(
    private val name: String,
    private val events: MutableList<String>,
    private val closeFailure: Throwable? = null,
) : AutoCloseable {
    override fun close() {
        events += "$name.close"
        closeFailure?.let { throw it }
    }
}
