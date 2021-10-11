package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.user.UserId;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    public record MembershipPKDTO(PostId postId, GroupId groupId, UserId userId) { }

    @Override
    public MembershipPK.MembershipPKDTO getValue() {
        return new MembershipPKDTO(
                this.post.domainId(),
                this.group.domainId(),
                this.user.domainId()
        );
    }
}
