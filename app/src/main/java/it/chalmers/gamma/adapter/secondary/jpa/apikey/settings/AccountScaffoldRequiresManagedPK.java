package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class AccountScaffoldRequiresManagedPK
    extends PKId<AccountScaffoldRequiresManagedPK.RequiresManagedPK> {

  @Column(name = "settings_id", columnDefinition = "uuid")
  private UUID settingsId;

  @Column(name = "super_group_type_name")
  private String type;

  @Override
  public RequiresManagedPK getValue() {
    return new RequiresManagedPK(this.settingsId, this.type);
  }

  protected AccountScaffoldRequiresManagedPK() {}

  protected AccountScaffoldRequiresManagedPK(UUID settingsId, String type) {
    this.settingsId = settingsId;
    this.type = type;
  }

  public record RequiresManagedPK(UUID settingsId, String type) {}
}
