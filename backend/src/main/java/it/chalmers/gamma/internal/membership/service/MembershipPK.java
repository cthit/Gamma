package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.user.service.UserId;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class MembershipPK extends Id<MembershipPK.MembershipPKDTO> {

    @Override
    protected MembershipPKDTO get() {
        return new MembershipPKDTO(
                this.postId,
                this.groupId,
                this.userId
        );
    }

    protected record MembershipPKDTO(PostId postId, GroupId groupId, UserId userId) { }

    @Embedded
    private PostId postId;

    @Embedded
    private GroupId groupId;

    @Embedded
    private UserId userId;

    protected MembershipPK() {

    }

    public MembershipPK(PostId postId, GroupId groupId, UserId userId) {
        this.postId = postId;
        this.groupId = groupId;
        this.userId = userId;
    }

}
