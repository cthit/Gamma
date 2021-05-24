package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.internal.text.service.TextEntity;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId, ClientDTO> {

    @EmbeddedId
    private ClientId clientId;

    @Embedded
    private ClientSecret clientSecret;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TextEntity description;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "auto_approve")
    private boolean autoApprove;

    @Embedded
    private Name name;

    protected ClientEntity() { }

    protected ClientEntity(ClientDTO client) {
        assert(client.clientId() != null);

        this.clientId = client.clientId();
        this.clientSecret = client.clientSecret();
        this.description = new TextEntity(client.description());
        this.webServerRedirectUri = client.webServerRedirectUri();
        this.autoApprove = client.autoApprove();
        this.name = client.name();
        this.description.apply(client.description());
    }

    @Override
    protected ClientId id() {
        return this.clientId;
    }

    @Override
    protected ClientDTO toDTO() {
        return new ClientDTO(
                this.clientId,
                this.clientSecret,
                this.webServerRedirectUri,
                this.autoApprove,
                this.name,
                this.description.toDTO()
        );
    }
}
