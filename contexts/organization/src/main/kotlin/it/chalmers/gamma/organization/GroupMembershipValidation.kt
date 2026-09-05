package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId

internal fun requireUniqueMemberships(memberships: List<NewGroupMembership>) {
    val assignments = mutableSetOf<MembershipAssignment>()
    for (membership in memberships) {
        if (!assignments.add(MembershipAssignment(membership.userId, membership.postId))) {
            throw OrganizationConflict("A user cannot hold the same post twice in a group")
        }
    }
}

private data class MembershipAssignment(
    val userId: UserId,
    val postId: PostId,
)
