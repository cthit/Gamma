package it.chalmers.gamma.domain.membership.data;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.IDsNotMatchingException;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name = "membership")
public class Membership implements GEntity<MembershipShallowDTO> {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "unofficial_post_name")
    private String unofficialPostName;

    public Membership(MembershipShallowDTO membership) {
        try {
            apply(membership);
        } catch (IDsNotMatchingException e) {
            e.printStackTrace();
        }
    }

    public MembershipPK getId() {
        return this.id;
    }

    public void setId(MembershipPK id) {
        this.id = id;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "Membership{"
            + "id=" + this.id
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + '}';
    }

    @Override
    public void apply(MembershipShallowDTO m) throws IDsNotMatchingException {
        if(this.id.getGroupId() != m.getGroupId()
            || this.id.getUserId() != m.getUserId()) {
            throw new IDsNotMatchingException();
        }

        this.unofficialPostName = m.getUnofficialPostName();
        this.postId = m.getPostId();
    }
}
