package it.chalmers.gamma.app.membership.service;

import it.chalmers.gamma.adapter.secondary.jpa.util.Id;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.UserId;

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
