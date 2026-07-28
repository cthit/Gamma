package it.chalmers.gamma.app.apikey.domain;

public enum Scope {
  PROFILES_READ("profiles:read"),
  DIRECTORY_READ("directory:read"),
  SUPER_GROUPS_READ("super-groups:read"),
  GROUPS_READ("groups:read"),
  MEMBERSHIPS_READ("memberships:read"),
  ALLOWLIST_WRITE("allowlist:write"),
  ACCOUNTS_PROVISION("accounts:provision"),
  CLIENTS_SELF("clients:self");

  private final String value;

  Scope(String value) {
    this.value = value;
  }

  public static Scope fromValue(String value) {
    for (Scope scope : values()) {
      if (scope.value.equals(value)) {
        return scope;
      }
    }
    throw new IllegalArgumentException("Unknown scope: " + value);
  }
}
