package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.internal.text.data.db.Text;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class Client extends ImmutableEntity<ClientId, ClientDTO> {

    @EmbeddedId
    private ClientId clientId;

    @Embedded
    private ClientSecret clientSecret;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "auto_approve")
    private boolean autoApprove;

    @Column(name = "name")
    private String name;

    protected Client() { }

    protected Client(ClientDTO client) {
        assert(client.clientId() != null);

        this.clientId = client.clientId();
        this.clientSecret = client.clientSecret();
        this.description = new Text(client.description());
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

    @Override
    public String toString() {
        return "Client{"
                + ", clientId='" + this.clientId + '\''
                + ", clientSecret={redacted}'''"
                + ", webServerRedirectUri='" + this.webServerRedirectUri + '\''
                + ", autoApprove=" + this.autoApprove
                + ", name='" + this.name + '\''
                + ", description='" + this.description + '\''
                + '}';
    }
}
