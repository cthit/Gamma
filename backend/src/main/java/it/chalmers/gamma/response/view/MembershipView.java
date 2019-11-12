package it.chalmers.gamma.response.view;

import it.chalmers.gamma.db.entity.Post;

import java.util.Objects;

public class MembershipView {
    private final Post post;
    private final String unofficialPostName;

    public MembershipView(Post post, String unofficialPostName) {
        this.post = post;
        this.unofficialPostName = unofficialPostName;
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
        MembershipView that = (MembershipView) o;
        return Objects.equals(this.post, that.post)
                && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "MembershipView{"
                + "post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + '}';
    }
}
