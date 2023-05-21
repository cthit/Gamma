package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.PostFK;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionPostPK extends PKId<ClientRestrictionPostPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @Embedded
    private PostFK postFK;

    protected ClientRestrictionPostPK() {
    }

    protected ClientRestrictionPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, ClientEntity clientEntity) {
        this.postFK = new PostFK(superGroupEntity, postEntity);
        this.client = clientEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientUid(this.client.getId()),
                new SuperGroupId(this.postFK.getSuperGroupEntity().getId()),
                new PostId(this.postFK.getPostEntity().getId())
        );
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            SuperGroupId superGroupId,
                                            PostId postId) {
    }

}
