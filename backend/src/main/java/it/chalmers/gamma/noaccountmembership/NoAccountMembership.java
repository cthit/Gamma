package it.chalmers.gamma.noaccountmembership;

import it.chalmers.gamma.post.Post;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "no_account_membership")
public class NoAccountMembership {

    @EmbeddedId
    private NoAccountMembershipPK id;

    @JoinColumn(name = "post_id")
    private UUID postId;

    @Column(name = "unofficial_post_name", length = 100)
    private String unofficialPostName;

    public NoAccountMembershipPK getId() {
        return this.id;
    }

}
