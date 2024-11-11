package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountScaffoldRequiresManagedJpaRepository
    extends JpaRepository<AccountScaffoldRequiresManagedEntity, AccountScaffoldRequiresManagedPK> {}
