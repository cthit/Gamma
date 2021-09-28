package it.chalmers.gamma.adapter.secondary.jpa.client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, String> { }
