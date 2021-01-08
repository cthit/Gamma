package it.chalmers.gamma.post.request;

import it.chalmers.gamma.db.entity.Text;
import java.util.Objects;
import javax.validation.constraints.NotNull;


public class AddPostRequest {
    @NotNull(message = "POST_CANNOT_BE_NULL")
    private Text post;

    private String emailPrefix;

    public Text getPost() {
        return this.post;
    }

    public void setPost(Text post) {
        this.post = post;
    }

    public String getEmailPrefix() {
        return this.emailPrefix;
    }

    public void setEmailPrefix(String emailPrefix) {
        this.emailPrefix = emailPrefix;
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
        return Objects.equals(this.post, that.post)
            && Objects.equals(this.emailPrefix, that.emailPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.emailPrefix);
    }

    @Override
    public String toString() {
        return "AddPostRequest{"
            + "post=" + this.post
            + ", emailPrefix='" + this.emailPrefix + '\''
            + '}';
    }
}
