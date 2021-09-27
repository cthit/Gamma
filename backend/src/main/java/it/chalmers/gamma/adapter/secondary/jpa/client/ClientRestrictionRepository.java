package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRestrictionRepository extends JpaRepository<ClientRestrictionEntity, ClientRestrictionPK> {

    public List<ClientRestrictionEntity> findClientRestrictionsById_ClientId(ClientId clientId);

}
