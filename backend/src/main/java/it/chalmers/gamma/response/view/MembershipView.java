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
        return post;
    }

    public String getUnofficialPostName() {
        return unofficialPostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipView that = (MembershipView) o;
        return Objects.equals(post, that.post) &&
                Objects.equals(unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, unofficialPostName);
    }

    @Override
    public String toString() {
        return "MembershipView{" +
                "post=" + post +
                ", unofficialPostName='" + unofficialPostName + '\'' +
                '}';
    }
}
