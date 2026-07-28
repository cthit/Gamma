package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ApiKeySuperGroupTypePK implements Serializable {

  @Column(name = "api_key_id", columnDefinition = "uuid")
  protected UUID apiKeyId;

  @Column(name = "super_group_type_name")
  protected String superGroupTypeName;

  public ApiKeySuperGroupTypePK() {}

  public ApiKeySuperGroupTypePK(UUID apiKeyId, String superGroupTypeName) {
    this.apiKeyId = apiKeyId;
    this.superGroupTypeName = superGroupTypeName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ApiKeySuperGroupTypePK that = (ApiKeySuperGroupTypePK) o;
    return Objects.equals(apiKeyId, that.apiKeyId)
        && Objects.equals(superGroupTypeName, that.superGroupTypeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKeyId, superGroupTypeName);
  }
}
