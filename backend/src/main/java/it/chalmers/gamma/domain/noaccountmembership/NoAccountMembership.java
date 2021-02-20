package it.chalmers.gamma.domain.noaccountmembership;

import it.chalmers.gamma.domain.post.PostId;

import java.util.UUID;

import javax.persistence.*;


@Entity
@Table(name = "no_account_membership")
public class NoAccountMembership {

    @EmbeddedId
    private NoAccountMembershipPK id;

    @Embedded
    private PostId postId;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    public NoAccountMembershipPK getId() {
        return this.id;
    }

}
