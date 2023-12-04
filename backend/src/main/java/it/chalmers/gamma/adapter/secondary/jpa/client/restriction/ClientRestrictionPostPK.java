package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.PostFK;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionPostPK extends PKId<ClientRestrictionPostPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "restriction_id")
    private ClientRestrictionEntity clientRestriction;

    @Embedded
    private PostFK postFK;

    protected ClientRestrictionPostPK() {
    }

    protected ClientRestrictionPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, ClientRestrictionEntity clientRestrictionEntity) {
        this.postFK = new PostFK(superGroupEntity, postEntity);
        this.clientRestriction = clientRestrictionEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientRestrictionId(this.clientRestriction.restrictionId),
                new SuperGroupId(this.postFK.getSuperGroupEntity().getId()),
                new PostId(this.postFK.getPostEntity().getId())
        );
    }

    protected record ClientRestrictionPKDTO(ClientRestrictionId clientRestrictionId,
                                            SuperGroupId superGroupId,
                                            PostId postId) {
    }

}
