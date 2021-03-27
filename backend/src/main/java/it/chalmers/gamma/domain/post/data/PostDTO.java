package it.chalmers.gamma.domain.post.data;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.Objects;

public class PostDTO implements DTO {

    private final PostId id;

    @JsonUnwrapped
    private final TextDTO postName;
    private final String emailPrefix;

    public PostDTO(TextDTO postName, String emailPrefix) {
        this(new PostId(), postName, emailPrefix);
    }

    public PostDTO(PostId id, TextDTO postName, String emailPrefix) {
        this.id = id;
        this.postName = postName;
        this.emailPrefix = emailPrefix;
    }

    public PostId getId() {
        return this.id;
    }

    public TextDTO getPostName() {
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
