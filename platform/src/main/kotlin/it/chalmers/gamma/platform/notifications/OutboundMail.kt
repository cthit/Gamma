package it.chalmers.gamma.platform.notifications

data class MailMessage(
    val to: String,
    val subject: String,
    val body: String,
    val sourceAddress: String? = null,
) {
    override fun toString(): String = "MailMessage(to=<redacted>, subject=<redacted>, body=<redacted>)"
}

fun interface OutboundMail {
    fun send(message: MailMessage)
}

object DiscardingOutboundMail : OutboundMail {
    override fun send(message: MailMessage) = Unit
}
