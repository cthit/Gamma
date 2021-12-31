package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<UUID> {

    @Id
    @Column(name = "client_uid")
    protected UUID clientUid;

    @Column(name = "client_id")
    protected String clientId;

    @Column(name = "client_secret")
    protected String clientSecret;

    @Column(name = "web_server_redirect_uri")
    protected String webServerRedirectUrl;

    @Column(name = "pretty_name")
    protected String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity description;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionEntity> restrictions;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientScopeEntity> scopes;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<UserApprovalEntity> approvals;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ClientApiKeyEntity clientsApiKey;

    protected ClientEntity() {
        this.approvals = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.scopes = new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return this.clientUid;
    }

}
