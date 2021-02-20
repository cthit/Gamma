package it.chalmers.gamma.domain.post.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.text.Text;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    private final PostId id;

    @JsonUnwrapped
    private final Text postName;
    private final String emailPrefix;

    public PostDTO(Text postName, String emailPrefix) {
        this.id = null;
        this.postName = postName;
        this.emailPrefix = emailPrefix;
    }

    public PostDTO(PostId id, Text postName, String emailPrefix) {
        this.id = id;
        this.postName = postName;
        this.emailPrefix = emailPrefix;
    }

    public PostId getId() {
        return this.id;
    }

    public Text getPostName() {
        return this.postName;
    }

    public String getEmailPrefix() {
        return this.emailPrefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostDTO postDTO = (PostDTO) o;
        return Objects.equals(this.id, postDTO.id)
                && Objects.equals(this.postName, postDTO.postName)
                && Objects.equals(this.emailPrefix, postDTO.emailPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.postName, this.emailPrefix);
    }

    @Override
    public String toString() {
        return "PostDTO{"
                + "id=" + this.id
                + ", postName=" + this.postName
                + ", emailPrefix=" + this.emailPrefix
                + '}';
    }

}
