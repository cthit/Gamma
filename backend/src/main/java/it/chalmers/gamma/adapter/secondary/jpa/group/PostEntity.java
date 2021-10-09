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
    protected UUID id;

    @JoinColumn(name = "post_name")
    @OneToOne(cascade = CascadeType.MERGE)
    protected TextEntity postName;

    @Column(name = "email_prefix")
    protected String emailPrefix;

    protected PostEntity() { }

    protected PostEntity(UUID id) {
        this.id = id;
    }

    @Override
    public PostId id() {
        return new PostId(this.id);
    }

}
