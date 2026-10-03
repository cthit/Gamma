package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import jakarta.persistence.*;

@Entity
@Table(name = "g_api_key_super_group_type")
public class ApiKeySuperGroupTypeEntity {

  @EmbeddedId protected ApiKeySuperGroupTypePK id;

  @Column(name = "gdpr_filter", nullable = false)
  protected boolean gdprFilter;

  public ApiKeySuperGroupTypeEntity() {}

  public ApiKeySuperGroupTypeEntity(ApiKeySuperGroupTypePK id, boolean gdprFilter) {
    this.id = id;
    this.gdprFilter = gdprFilter;
  }

  public ApiKeySuperGroupTypePK getId() {
    return id;
  }

  public boolean isGdprFilter() {
    return gdprFilter;
  }

  public void setGdprFilter(boolean gdprFilter) {
    this.gdprFilter = gdprFilter;
  }
}
