package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

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
public class ClientAuthorityPostPK extends PKId<ClientAuthorityPostPK.AuthorityPostPKRecord> {

    @Embedded
    protected PostFK postFK;

    @Embedded
    protected ClientAuthorityEntityPK clientAuthority;

    protected ClientAuthorityPostPK() {
    }

    public ClientAuthorityPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, ClientAuthorityEntity clientAuthority) {
        this.postFK = new PostFK(superGroupEntity, postEntity);
        this.clientAuthority = clientAuthority.getId();
    }

    @Override
    public AuthorityPostPKRecord getValue() {
        return new AuthorityPostPKRecord(
                new SuperGroupId(this.postFK.getSuperGroupEntity().getId()),
                new PostId(this.postFK.getPostEntity().getId()),
                new AuthorityName(this.clientAuthority.name),
                new ClientUid(this.clientAuthority.client.getId())
        );
    }

    public record AuthorityPostPKRecord(SuperGroupId superGroupId,
                                        PostId postId,
                                        AuthorityName authorityName,
                                        ClientUid clientUid) {
    }

}
