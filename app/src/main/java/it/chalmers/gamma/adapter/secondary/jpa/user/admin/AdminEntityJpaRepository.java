package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminEntityJpaRepository extends JpaRepository<AdminEntity, UUID> {}
