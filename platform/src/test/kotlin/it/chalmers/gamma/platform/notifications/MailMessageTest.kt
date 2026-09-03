package it.chalmers.gamma.platform.notifications

import kotlin.test.Test
import kotlin.test.assertEquals

class MailMessageTest {
    @Test
    fun `mail messages redact personal data in diagnostics`() {
        val message = MailMessage("private@example.org", "Private subject", "Private body")

        assertEquals(
            "MailMessage(to=<redacted>, subject=<redacted>, body=<redacted>)",
            message.toString(),
        )
    }

    @Test
    fun `discarding mail delivery completes without an external effect`() =
        run {
            assertEquals(
                Unit,
                DiscardingOutboundMail.send(
                    MailMessage("private@example.org", "Private subject", "Private body"),
                ),
            )
        }
}
