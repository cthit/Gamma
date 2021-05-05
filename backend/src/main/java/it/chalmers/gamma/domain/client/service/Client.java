package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.text.data.db.Text;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class Client extends MutableEntity<ClientDTO> {

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

        if(this.description == null) {
            this.description = new Text();
        }

        apply(client);
    }

    @Override
    protected void apply(ClientDTO c) {
        assert(this.clientId == c.clientId());

        this.webServerRedirectUri = c.webServerRedirectUri();
        this.autoApprove = c.autoApprove();
        this.name = c.name();
        this.description.apply(c.description());
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
