package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "g_post")
public class PostEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "post_id", columnDefinition = "uuid")
    protected UUID id;

    @JoinColumn(name = "post_name")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity postName;

    @Column(name = "email_prefix")
    protected String emailPrefix;

    protected PostEntity() {
    }

    protected PostEntity(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

}
