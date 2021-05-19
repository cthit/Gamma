package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.internal.client.service.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRestrictionRepository extends JpaRepository<ClientRestriction, ClientRestrictionPK> {

    public List<ClientRestriction> findClientRestrictionsById_ClientId(ClientId clientId);

}
