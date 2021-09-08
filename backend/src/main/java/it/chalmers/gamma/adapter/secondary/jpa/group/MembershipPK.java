package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class MembershipPK extends Id<MembershipPK.MembershipPKDTO> {

    @Override
    public MembershipPKDTO value() {
        return new MembershipPKDTO(
                PostId.valueOf(this.postId),
                GroupId.valueOf(this.groupId),
                UserId.valueOf(this.userId)
        );
    }

    protected record MembershipPKDTO(PostId postId, GroupId groupId, UserId userId) { }

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "user_id")
    private UUID userId;

    protected MembershipPK() {

    }

    public MembershipPK(PostId postId, GroupId groupId, UserId userId) {
        this.postId = postId.value();
        this.groupId = groupId.value();
        this.userId = userId.value();
    }

}
