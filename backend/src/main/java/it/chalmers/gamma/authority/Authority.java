package it.chalmers.gamma.authority;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "authority")
public class Authority {

    @Column(name = "id")
    private UUID id;

    @JoinColumn(name = "fkit_group_id")
    private UUID superGroupId;

    @JoinColumn(name = "post_id")
    private UUID postId;

    @JoinColumn(name = "authority_level")
    private UUID authorityLevelId;

    public Authority(UUID id, UUID superGroupId, UUID postId, UUID authorityLevelId) {
        this.id = id;
        this.superGroupId = superGroupId;
        this.postId = postId;
        this.authorityLevelId = authorityLevelId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSuperGroupId() {
        return superGroupId;
    }

    public void setSuperGroupId(UUID superGroupId) {
        this.superGroupId = superGroupId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getAuthorityLevelId() {
        return authorityLevelId;
    }

    public void setAuthorityLevelId(UUID authorityLevelId) {
        this.authorityLevelId = authorityLevelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id) && Objects.equals(superGroupId, authority.superGroupId) && Objects.equals(postId, authority.postId) && Objects.equals(authorityLevelId, authority.authorityLevelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, superGroupId, postId, authorityLevelId);
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", superGroupId=" + superGroupId +
                ", postId=" + postId +
                ", authorityLevelId=" + authorityLevelId +
                '}';
    }
}
