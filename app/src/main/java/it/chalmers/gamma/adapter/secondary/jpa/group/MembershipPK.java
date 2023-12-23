package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class MembershipPK extends PKId<MembershipPK.MembershipPKDTO> {

    @JoinColumn(name = "post_id")
    @ManyToOne
    private PostEntity post;

    @JoinColumn(name = "group_id")
    @ManyToOne
    private GroupEntity group;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;

    protected MembershipPK() {

    }

    public MembershipPK(PostEntity post,
                        GroupEntity group,
                        UserEntity user) {
        this.post = post;
        this.group = group;
        this.user = user;
    }

    public PostEntity getPost() {
        return post;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public MembershipPK.MembershipPKDTO getValue() {
        return new MembershipPKDTO(
                new PostId(this.post.getId()),
                new GroupId(this.group.getId()),
                new UserId(this.user.getId())
        );
    }

    public record MembershipPKDTO(PostId postId, GroupId groupId, UserId userId) {
    }
}
