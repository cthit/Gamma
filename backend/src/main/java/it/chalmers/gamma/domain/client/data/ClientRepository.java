package it.chalmers.gamma.domain.client.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 *  String is the client id
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> { }
