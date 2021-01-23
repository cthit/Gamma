package it.chalmers.gamma.noaccountmembership;

import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.post.Post;
import java.util.Objects;

public class NoAccountMembershipDTO {
    private final String cid;
    private final Group group;
    private final Post post;
    private final String unofficialPostName;


    public NoAccountMembershipDTO(String cid, Group group, Post post, String unofficialPostName) {
        this.cid = cid;
        this.group = group;
        this.post = post;
        this.unofficialPostName = unofficialPostName;
    }

    public String getCid() {
        return this.cid;
    }

    public Group getGroup() {
        return this.group;
    }

    public Post getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoAccountMembershipDTO that = (NoAccountMembershipDTO) o;
        return Objects.equals(this.cid, that.cid)
                && Objects.equals(this.group, that.group)
                && Objects.equals(this.post, that.post)
                && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cid, this.group, this.post, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "NoAccountMembershipDTO{"
                + "cid='" + this.cid + '\''
                + ", group=" + this.group
                + ", post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + '}';
    }
}
