package it.chalmers.gamma.app.post.service;

import it.chalmers.gamma.app.domain.EmailPrefix;
import it.chalmers.gamma.app.domain.Post;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.service.TextEntity;
import it.chalmers.gamma.util.entity.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class PostEntity extends MutableEntity<PostId, Post> {

    @EmbeddedId
    private PostId id;

    @JoinColumn(name = "post_name")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity postName;

    @Column(name = "email_prefix")
    private EmailPrefix emailPrefix;

    protected PostEntity() { }

    protected PostEntity(Post p) {
        assert(p.id() != null);

        this.id = p.id();
        this.postName = new TextEntity();

        apply(p);
    }

    @Override
    public void apply(Post p) {
        assert(this.id == p.id());

        this.postName.apply(p.name());
        this.emailPrefix = p.emailPrefix();
    }

    @Override
    protected Post toDTO() {
        return new Post(
                this.id,
                this.postName.toDTO(),
                this.emailPrefix
        );
    }

    @Override
    protected PostId id() {
        return this.id;
    }

}
