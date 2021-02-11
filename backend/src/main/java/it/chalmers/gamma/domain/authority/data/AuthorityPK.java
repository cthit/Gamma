package it.chalmers.gamma.domain.authority.data;

import it.chalmers.gamma.domain.post.data.Post;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Embeddable
public class AuthorityPK {

    @JoinColumn(name = "fkit_group_id")
    private UUID superGroupId;

    @JoinColumn(name = "post_id")
    private UUID postId;

    @JoinColumn(name = "authority_level")
    private String authorityLevelName;

    protected AuthorityPK() {}

    public AuthorityPK(UUID superGroupId, UUID postId, String authorityLevelName) {
        this.superGroupId = superGroupId;
        this.postId = postId;
        this.authorityLevelName = authorityLevelName;
    }

    public UUID getSuperGroupId() {
        return superGroupId;
    }

    public UUID getPostId() {
        return postId;
    }

    public String getAuthorityLevelName() {
        return authorityLevelName;
    }
}
