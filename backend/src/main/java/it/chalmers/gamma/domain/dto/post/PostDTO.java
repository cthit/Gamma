package it.chalmers.gamma.domain.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {
    private final UUID id;
    private final Text postName;

    public PostDTO(UUID id, Text postName) {
        this.id = id;
        this.postName = postName;
    }

    public UUID getId() {
        return id;
    }

    public Text getPostName() {
        return postName;
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
                && Objects.equals(this.postName, postDTO.postName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.postName);
    }

    @Override
    public String toString() {
        return "PostDTO{"
                + "id=" + id
                + ", postName=" + postName
                + '}';
    }
}
