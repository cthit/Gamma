package it.chalmers.gamma.internal.clientrestriction.service;

import it.chalmers.gamma.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRestrictionRepository extends JpaRepository<ClientRestrictionEntity, ClientRestrictionPK> {

    public List<ClientRestrictionEntity> findClientRestrictionsById_ClientId(ClientId clientId);

}
