package it.chalmers.gamma.internal.post.service;

import it.chalmers.gamma.internal.text.data.db.Text;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class Post extends MutableEntity<PostId, PostDTO> {

    @EmbeddedId
    private PostId id;

    @JoinColumn(name = "post_name")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text postName;

    @Column(name = "email_prefix")
    private EmailPrefix emailPrefix;

    protected Post() { }

    protected Post(PostDTO p) {
        assert(p.id() != null);

        this.id = p.id();
        this.postName = new Text();

        apply(p);
    }

    @Override
    public void apply(PostDTO p) {
        assert(this.id == p.id());

        this.postName.apply(p.name());
        this.emailPrefix = p.emailPrefix();
    }

    @Override
    protected PostDTO toDTO() {
        return new PostDTO(
                this.id,
                this.postName.toDTO(),
                this.emailPrefix
        );
    }

    @Override
    protected PostId id() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Post{"
            + "id=" + this.id
            + ", name=" + this.postName
            + ", emailPrefix='" + this.emailPrefix + '\''
            + '}';
    }
}
