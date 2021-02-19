package it.chalmers.gamma.domain.noaccountmembership;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "no_account_membership")
public class NoAccountMembership {

    @EmbeddedId
    private NoAccountMembershipPK id;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    public NoAccountMembershipPK getId() {
        return this.id;
    }

}
