package it.chalmers.gamma.domain.post.data;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.text.data.db.Text;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class Post implements MutableEntity<PostDTO> {

    @EmbeddedId
    private PostId id;

    @JoinColumn(name = "post_name")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text postName;

    @Column(name = "email_prefix")
    private String emailPrefix;

    protected Post() { }

    public Post(PostDTO p) {
        assert(p.getId() != null);

        this.id = p.getId();

        if(this.postName == null) {
            this.postName = new Text();
        }

        apply(p);
    }

    @Override
    public void apply(PostDTO p) {
        assert(this.id == p.getId());

        this.postName.apply(p.getPostName());
        this.emailPrefix = p.getEmailPrefix();
    }

    @Override
    public PostDTO toDTO() {
        return new PostDTO(
                this.id,
                this.postName.toDTO(),
                this.emailPrefix
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(this.id, post.id)
            && Objects.equals(this.postName, post.postName)
            && Objects.equals(this.emailPrefix, post.emailPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.postName, this.emailPrefix);
    }

    @Override
    public String toString() {
        return "Post{"
            + "id=" + this.id
            + ", postName=" + this.postName
            + ", emailPrefix='" + this.emailPrefix + '\''
            + '}';
    }
}
