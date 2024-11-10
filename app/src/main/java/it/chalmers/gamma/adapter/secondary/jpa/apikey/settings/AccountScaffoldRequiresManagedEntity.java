package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "g_api_key_account_scaffold_requires_managed")
public class AccountScaffoldRequiresManagedEntity
    extends ImmutableEntity<AccountScaffoldRequiresManagedPK> {

  @EmbeddedId AccountScaffoldRequiresManagedPK id;

  protected AccountScaffoldRequiresManagedEntity() {}

  protected AccountScaffoldRequiresManagedEntity(UUID settingsId, String type) {
    this.id = new AccountScaffoldRequiresManagedPK(settingsId, type);
  }

  @Override
  public AccountScaffoldRequiresManagedPK getId() {
    return this.id;
  }
}
