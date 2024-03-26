package it.chalmers.gamma.adapter.secondary.jpa.client.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "g_client_api_key")
public class ClientApiKeyEntity extends ImmutableEntity<UUID> {

  @Id
  @Column(name = "client_uid", columnDefinition = "uuid")
  private UUID clientUid;

  @OneToOne
  @PrimaryKeyJoinColumn(name = "client_uid")
  private ClientEntity client;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "api_key_id")
  private ApiKeyEntity apiKey;

  protected ClientApiKeyEntity() {}

  public ClientApiKeyEntity(ClientEntity clientEntity, ApiKeyEntity apiKeyEntity) {
    this.client = clientEntity;
    this.clientUid = clientEntity.getId();
    this.apiKey = apiKeyEntity;
  }

  @Override
  public UUID getId() {
    return this.clientUid;
  }

  public ClientEntity getClient() {
    return this.client;
  }

  public ApiKeyEntity getApiKeyEntity() {
    return this.apiKey;
  }

  public void removeApiKey() {
    this.apiKey = null;
  }
}
