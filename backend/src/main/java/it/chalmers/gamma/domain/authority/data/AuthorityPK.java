package it.chalmers.gamma.domain.authority.data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class AuthorityPK implements Serializable {

    @Column(name = "fkit_group_id")
    private UUID superGroupId;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "authority_level")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityPK that = (AuthorityPK) o;
        return Objects.equals(superGroupId, that.superGroupId) && Objects.equals(postId, that.postId) && Objects.equals(authorityLevelName, that.authorityLevelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superGroupId, postId, authorityLevelName);
    }
}
