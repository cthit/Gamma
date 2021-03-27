package it.chalmers.gamma.domain.client.data.db;

import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.client.domain.ClientSecret;
import it.chalmers.gamma.domain.text.data.db.Text;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class Client implements MutableEntity<ClientDTO> {

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

    public Client(ClientDTO client) {
        assert(client.getClientId() != null);

        this.clientId = client.getClientId();
        this.clientSecret = client.getClientSecret();

        apply(client);
    }

    @Override
    public void apply(ClientDTO c) {
        assert(this.clientId == c.getClientId());

        this.webServerRedirectUri = c.getWebServerRedirectUri();
        this.autoApprove = c.isAutoApprove();
        this.name = c.getName();
        this.description.apply(c.getDescription());
    }

    @Override
    public ClientDTO toDTO() {
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
                + ", clientSecret={redacted}'\''"
                + ", webServerRedirectUri='" + this.webServerRedirectUri + '\''
                + ", autoApprove=" + this.autoApprove
                + ", name='" + this.name + '\''
                + ", description='" + this.description + '\''
                + '}';
    }
}
