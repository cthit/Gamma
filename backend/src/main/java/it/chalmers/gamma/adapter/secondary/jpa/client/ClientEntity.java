package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.client.restriction.ClientRestrictionEntity;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "g_client")
public class ClientEntity extends ImmutableEntity<UUID> {

    @Id
    @Column(name = "client_uid", columnDefinition = "uuid")
    protected UUID clientUid;

    @Column(name = "client_id")
    protected String clientId;

    @Column(name = "client_secret")
    protected String clientSecret;

    @Column(name = "redirect_uri")
    protected String webServerRedirectUrl;

    @Column(name = "pretty_name")
    protected String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity description;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientScopeEntity> scopes;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ClientApiKeyEntity clientsApiKey;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ClientRestrictionEntity clientRestriction;

    protected ClientEntity() {
        this.scopes = new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return this.clientUid;
    }

}
