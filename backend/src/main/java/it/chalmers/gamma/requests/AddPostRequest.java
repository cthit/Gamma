package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;

import java.util.Objects;

public class AddPostRequest {
    private Text post;
    //TODO ADD LEVEL OF CLEARANCE OF SOMETHING

    public Text getPost() {
        return this.post;
    }

    public void setPost(Text post) {
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
        AddPostRequest that = (AddPostRequest) o;
        return this.post.equals(that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post);
    }

    @Override
    public String toString() {
        return "AddPostRequest{"
            + "post=" + this.post
            + '}';
    }
}
