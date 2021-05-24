package it.chalmers.gamma.internal.post.service;

import it.chalmers.gamma.domain.EmailPrefix;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.internal.text.service.TextEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class PostEntity extends MutableEntity<PostId, PostDTO> {

    @EmbeddedId
    private PostId id;

    @JoinColumn(name = "post_name")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TextEntity postName;

    @Column(name = "email_prefix")
    private EmailPrefix emailPrefix;

    protected PostEntity() { }

    protected PostEntity(PostDTO p) {
        assert(p.id() != null);

        this.id = p.id();
        this.postName = new TextEntity();

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

}
