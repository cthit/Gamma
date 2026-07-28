package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.Scope;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "g_api_key")
public class ApiKeyEntity extends MutableEntity<UUID> {

  @Id
  @Column(name = "api_key_id", columnDefinition = "uuid")
  protected UUID id;

  @Column(name = "token")
  protected String token;

  @Column(name = "pretty_name")
  protected String prettyName;

  @Enumerated(EnumType.STRING)
  @Column(name = "key_type")
  protected ApiKeyType keyType;

  @JoinColumn(name = "description")
  @OneToOne(cascade = CascadeType.ALL)
  protected TextEntity description;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "g_api_key_scope", joinColumns = @JoinColumn(name = "api_key_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "scope")
  protected Set<Scope> scopes = new HashSet<>();

  public ApiKeyEntity() {
    description = new TextEntity();
  }

  public ApiKeyEntity(
      UUID id, String token, String prettyName, ApiKeyType keyType, TextEntity description) {
    this.id = id;
    this.token = token;
    this.prettyName = prettyName;
    this.keyType = keyType;
    this.description = description;
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  public void setApiKeyToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public String getPrettyName() {
    return prettyName;
  }

  public void setScopes(Set<Scope> scopes) {
    this.scopes = scopes;
  }

  public ApiKeyType getKeyType() {
    return keyType;
  }

  public TextEntity getDescription() {
    return description;
  }
}
