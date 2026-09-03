package it.chalmers.gamma.platform.notifications

internal class MailDeliveryFailure(
    cause: Throwable,
) : RuntimeException("Outbound mail delivery failed", cause)
