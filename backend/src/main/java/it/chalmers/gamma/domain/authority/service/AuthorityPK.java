package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

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

    protected SuperGroupId getSuperGroupId() {
        return superGroupId;
    }

    protected PostId getPostId() {
        return postId;
    }

    protected AuthorityLevelName getAuthorityLevelName() {
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
