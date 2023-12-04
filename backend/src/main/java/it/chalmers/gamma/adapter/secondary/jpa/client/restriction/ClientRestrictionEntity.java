package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "g_client_restriction")
public class ClientRestrictionEntity {

    @Id
    @Column(name = "restriction_id", columnDefinition = "uuid")
    protected UUID restrictionId;

    @Column(name = "client_uid", columnDefinition = "uuid")
    protected UUID clientUid;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "client_uid")
    protected ClientEntity client;

    @OneToMany(mappedBy = "id.clientRestriction", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionUserEntity> userRestrictions;

    @OneToMany(mappedBy = "id.clientRestriction", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionSuperGroupEntity> superGroupRestrictions;

    @OneToMany(mappedBy = "id.clientRestriction", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionPostEntity> superGroupPostRestrictions;


}
