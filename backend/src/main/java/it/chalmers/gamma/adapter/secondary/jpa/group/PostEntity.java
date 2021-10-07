package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.group.EmailPrefix;
import it.chalmers.gamma.app.domain.post.Post;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "post")
public class PostEntity extends MutableEntity<PostId> {

    @Id
    @Column(name = "post_id")
    private UUID id;

    @JoinColumn(name = "post_name")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity postName;

    @Column(name = "email_prefix")
    private String emailPrefix;

    protected PostEntity() { }

    public PostEntity(UUID id) {
        this.id = id;
    }

    public void apply(Post p) {
        assert(this.id == p.id().getValue());

        throwIfNotValidVersion(p.version());

        if (this.postName == null) {
            this.postName = new TextEntity();
        }

        this.postName.apply(p.name());
        this.emailPrefix = p.emailPrefix().value();
    }

    public Post toDomain() {
        return new Post(
                new PostId(this.id),
                this.getVersion(),
                this.postName.toDomain(),
                new EmailPrefix(this.emailPrefix)
        );
    }

    @Override
    protected PostId id() {
        return new PostId(this.id);
    }

}
