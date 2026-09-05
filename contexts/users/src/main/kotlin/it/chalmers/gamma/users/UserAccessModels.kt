package it.chalmers.gamma.users

/**
 * Owns complete access-flag state. Replacements validate every selected user and commit as one
 * operation; callers must not derive replacements from a paged directory view. Every operation
 * authorizes `administratorId` in the same transaction or consistent snapshot as its protected
 * work; the supplied identifier is not proof of authority.
 */
enum class UserAccessFlagKind {
    ADMINISTRATOR,
    GDPR_TRAINED,
}

data class UserAccessFlag(
    val userId: UserId,
    val firstName: FirstName,
    val nick: Nick,
    val lastName: LastName,
    val enabled: Boolean,
) {
    override fun toString(): String = "UserAccessFlag(<redacted>)"
}
