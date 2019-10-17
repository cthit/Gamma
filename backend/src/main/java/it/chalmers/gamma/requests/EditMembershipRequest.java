package it.chalmers.delta.requests;

import java.util.Objects;

public class EditMembershipRequest {

    private String post;
    private String unofficialName;

    public String getPost() {
        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

    @Override
    public String toString() {
        return "EditMembershipRequest{"
            + "unofficialName='" + this.unofficialName + '\''
            + "post='" + this.post + '\''
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
        EditMembershipRequest that = (EditMembershipRequest) o;
        return Objects.equals(this.unofficialName, that.unofficialName) && Objects.equals(this.post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.unofficialName, this.post);
    }
}
