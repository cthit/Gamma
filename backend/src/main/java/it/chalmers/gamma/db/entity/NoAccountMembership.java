package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.NoAccountMembershipPK;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoAccountMembership that = (NoAccountMembership) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(post, that.post) &&
                Objects.equals(unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, post, unofficialPostName);
    }

    @Override
    public String toString() {
        return "NoAccountMembership{" +
                "id=" + id +
                ", post=" + post +
                ", unofficialPostName='" + unofficialPostName + '\'' +
                '}';
    }
}
