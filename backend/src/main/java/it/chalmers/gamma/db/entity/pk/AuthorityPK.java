package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Post;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Embeddable
public class AuthorityPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITGroup fkitGroup;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private static final long serialVersionUID = 3085451407319206L;


    public FKITGroup getFkitGroup() {
        return this.fkitGroup;
    }

    public void setFkitGroup(FKITGroup fkitGroup) {
        this.fkitGroup = fkitGroup;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorityPK that = (AuthorityPK) o;
        return Objects.equals(this.fkitGroup, that.fkitGroup)
            && Objects.equals(this.post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fkitGroup, this.post);
    }

    @Override
    public String toString() {
        return "AuthorityPK{"
            + "fkitGroup=" + this.fkitGroup
            + ", post=" + this.post
            + '}';
    }
}
