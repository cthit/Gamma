package it.chalmers.gamma.db.entity.pk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Post;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class AuthorityPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITSuperGroup fkitSuperGroup;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private static final long serialVersionUID = 3085451407319206L;


    public FKITSuperGroup getFkitSuperGroup() {
        return this.fkitSuperGroup;
    }

    public void setFkitGroup(FKITSuperGroup fkitSuperGroup) {
        this.fkitSuperGroup = fkitSuperGroup;
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
        return Objects.equals(this.fkitSuperGroup, that.fkitSuperGroup)
            && Objects.equals(this.post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fkitSuperGroup, this.post);
    }

    @Override
    public String toString() {
        return "AuthorityPK{"
            + "fkitGroup=" + this.fkitSuperGroup
            + ", post=" + this.post
            + '}';
    }
}
