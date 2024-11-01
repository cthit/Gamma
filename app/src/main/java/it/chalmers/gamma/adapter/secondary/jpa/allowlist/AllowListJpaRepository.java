package it.chalmers.gamma.adapter.secondary.jpa.allowlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowListJpaRepository extends JpaRepository<AllowListEntity, String> {}
