package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminEntityJpaRepository extends JpaRepository<AdminEntity, UUID> {
}
