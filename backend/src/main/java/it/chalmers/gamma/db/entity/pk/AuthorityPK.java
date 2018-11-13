package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorityPK implements Serializable {
    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITGroup fkitGroup;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public FKITGroup getFkitGroup() {
        return fkitGroup;
    }

    public void setFkitGroup(FKITGroup fkitGroup) {
        this.fkitGroup = fkitGroup;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityPK that = (AuthorityPK) o;
        return Objects.equals(fkitGroup, that.fkitGroup) &&
                Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fkitGroup, post);
    }

    @Override
    public String toString() {
        return "AuthorityPK{" +
                "fkitGroup=" + fkitGroup +
                ", post=" + post +
                '}';
    }
}
