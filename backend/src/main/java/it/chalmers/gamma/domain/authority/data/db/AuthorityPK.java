package it.chalmers.gamma.domain.authority.data.db;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorityPK implements Serializable, Id {

    @Embedded
    private SuperGroupId superGroupId;

    @Embedded
    private PostId postId;

    @Embedded
    private AuthorityLevelName authorityLevelName;

    protected AuthorityPK() {}

    public AuthorityPK(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        this.superGroupId = superGroupId;
        this.postId = postId;
        this.authorityLevelName = authorityLevelName;
    }

    public SuperGroupId getSuperGroupId() {
        return superGroupId;
    }

    public PostId getPostId() {
        return postId;
    }

    public AuthorityLevelName getAuthorityLevelName() {
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
