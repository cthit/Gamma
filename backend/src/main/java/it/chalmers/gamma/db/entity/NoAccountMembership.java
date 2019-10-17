package it.chalmers.delta.db.entity;

import it.chalmers.delta.db.entity.pk.NoAccountMembershipPK;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "no_account_membership")
public class NoAccountMembership {

    @EmbeddedId
    private NoAccountMembershipPK id;

    @ManyToOne
    private Post post;

    @Column(name = "unofficial_post_name", length = 100)
    private String unofficialPostName;

    public NoAccountMembershipPK getId() {
        return this.id;
    }

    public void setId(NoAccountMembershipPK id) {
        this.id = id;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoAccountMembership that = (NoAccountMembership) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.post, that.post)
                && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.id, this.post, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "NoAccountMembership{"
                + "id=" + this.id
                + ", post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + '}';
    }
}
