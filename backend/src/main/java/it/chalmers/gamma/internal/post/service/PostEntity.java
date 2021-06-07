package it.chalmers.gamma.internal.post.service;

import it.chalmers.gamma.domain.EmailPrefix;
import it.chalmers.gamma.domain.Post;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.internal.text.service.TextEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class PostEntity extends MutableEntity<PostId, Post> {

    @EmbeddedId
    private PostId id;

    @JoinColumn(name = "post_name")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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
