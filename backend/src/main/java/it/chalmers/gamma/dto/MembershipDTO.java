package it.chalmers.gamma.response.view;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.db.entity.Post;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;

public class MembershipView {
    private final Post post;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final ITUserView user;

    public MembershipView(Post post,
                          String unofficialPostName,
                          ITUserView user) {
        this.post = post;
        this.unofficialPostName = unofficialPostName;
        this.user = user;
    }

    public Post getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public ITUserView getUser() {
        return user;
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
                && Objects.equals(this.unofficialPostName, that.unofficialPostName)
                && Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user);
    }

    @Override
    public String toString() {
        return "MembershipView{"
                + "post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + '}';
    }
}
