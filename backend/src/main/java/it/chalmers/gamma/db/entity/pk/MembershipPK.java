package it.chalmers.gamma.db.entity.pk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.user.ITUser;
import it.chalmers.gamma.post.Post;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class MembershipPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ituser_id")
    private ITUser itUser;

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private Group group;

    @ManyToOne
    private Post post;

    private static final long serialVersionUID = 6624119509779427L;


    public ITUser getITUser() {
        return this.itUser;
    }

    public void setITUser(ITUser ituserId) {
        this.itUser = ituserId;
    }

    public Group getFKITGroup() {
        return this.group;
    }

    public void setFKITGroup(Group groupId) {
        this.group = groupId;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "MembershipPK{"
                + "itUser=" + this.itUser
                + ", group=" + this.group
                + ", post=" + this.post
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(this.itUser, that.itUser)
                && Objects.equals(this.group, that.group)
                && Objects.equals(this.post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.itUser, this.group, this.post);
    }


}
