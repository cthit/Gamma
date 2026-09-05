package it.chalmers.gamma

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class ErrorMappingTest : SpringApplicationTest() {
    @Test
    fun `browser errors use stable status codes and generic messages`() {
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)

        val accessDenied = browser.get("/test/errors/access-denied")
        assertEquals(403, accessDenied.status)
        assertContains(accessDenied.body, "403 - Unauthorized")

        val conflict = browser.get("/test/errors/conflict")
        assertEquals(409, conflict.status)
        assertContains(conflict.body, "request conflicts with the current state")
        assertFalse(conflict.body.contains("sensitive conflict detail"))

        val invalid = browser.get("/test/errors/invalid")
        assertEquals(400, invalid.status)
        assertContains(invalid.body, "request was invalid")
        assertFalse(invalid.body.contains("sensitive invalid detail"))

        val (_, csrfToken) = browser.csrf("/")
        val upload = browser.form("POST", "/test/errors/upload", mapOf("_csrf" to csrfToken))
        assertEquals(413, upload.status)
        assertEquals("body", upload.header("HX-Retarget"))
        assertContains(upload.body, "uploaded file is too large")
    }

    @Test
    fun `unhandled browser error is logged without request parameters and rendered as html`() {
        val events = ListAppender<ILoggingEvent>().apply { start() }
        val logger = LoggerFactory.getLogger(ApplicationErrorController::class.java) as Logger
        logger.addAppender(events)

        try {
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login().status)
            val response =
                browser.get(
                    "/test/errors/unhandled?private=secret-value",
                    mapOf("Accept" to "text/html"),
                )

            assertEquals(500, response.status)
            assertContains(response.body, "500 - Internal Server Error")
            assertContains(response.body, "request could not be completed")
            assertFalse(response.body.contains("sensitive unhandled detail"))
            assertFalse(response.body.contains("secret-value"))

            val event = events.list.single()
            assertEquals("Unhandled request failed", event.formattedMessage)
            val failure = assertIs<ThrowableProxy>(event.throwableProxy).throwable
            assertIs<IllegalStateException>(failure)
            assertEquals("sensitive unhandled detail", failure.message)
            assertFalse(event.formattedMessage.contains("secret-value"))
        } finally {
            logger.detachAppender(events)
            events.stop()
        }
    }
}
