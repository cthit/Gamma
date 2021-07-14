package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.EmailPrefix;
import it.chalmers.gamma.app.domain.Post;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "post")
public class PostEntity extends MutableEntity<PostId, Post> {

    @Id
    @Column(name = "post_id")
    private UUID id;

    @JoinColumn(name = "post_name")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity postName;

    @Column(name = "email_prefix")
    private String emailPrefix;

    protected PostEntity() { }

    protected PostEntity(Post p) {
        assert(p.id() != null);

        this.id = p.id().value();
        this.postName = new TextEntity();

        apply(p);
    }

    @Override
    public void apply(Post p) {
        assert(this.id == p.id().value());

        this.postName.apply(p.name());
        this.emailPrefix = p.emailPrefix().value();
    }

    @Override
    protected Post toDomain() {
        return new Post(
                PostId.valueOf(this.id),
                this.postName.toDomain(),
                new EmailPrefix(this.emailPrefix)
        );
    }

    @Override
    protected PostId id() {
        return PostId.valueOf(this.id);
    }

}
