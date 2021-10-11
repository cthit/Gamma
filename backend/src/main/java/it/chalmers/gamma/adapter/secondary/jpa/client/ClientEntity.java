package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId> {

    @Id
    @Column(name = "client_id")
    protected String clientId;

    @Column(name = "client_secret")
    protected String clientSecret;

    @Column(name = "web_server_redirect_uri")
    protected String webServerRedirectUrl;

    @Column(name = "auto_approve")
    protected boolean autoApprove;

    @Column(name = "pretty_name")
    protected String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity description;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionEntity> restrictions;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<UserApprovalEntity> approvals;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ClientApiKeyEntity clientsApiKey;

    protected ClientEntity() {
        this.approvals = new ArrayList<>();
        this.restrictions = new ArrayList<>();
    }

    @Override
    public ClientId domainId() {
        return ClientId.valueOf(this.clientId);
    }

}
