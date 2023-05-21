package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class PostFK {

    @JoinColumn(name = "super_group_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private PostEntity postEntity;

    public PostFK() {
    }

    public PostFK(SuperGroupEntity superGroupEntity, PostEntity postEntity) {
        this.superGroupEntity = superGroupEntity;
        this.postEntity = postEntity;
    }

    public SuperGroupEntity getSuperGroupEntity() {
        return superGroupEntity;
    }

    public PostEntity getPostEntity() {
        return postEntity;
    }
}
