package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.Collections;

@Service
public class ClientRestrictionFinder implements GetEntity<ClientId, ClientRestrictionDTO> {

    private final ClientRestrictionRepository clientRestrictionRepository;

    public ClientRestrictionFinder(ClientRestrictionRepository clientRestrictionRepository) {
        this.clientRestrictionRepository = clientRestrictionRepository;
    }

    @Override
    public ClientRestrictionDTO get(ClientId clientId) throws EntityNotFoundException {
        return this.clientRestrictionRepository.findClientRestrictionsById_ClientId(clientId)
                .stream()
                .map(ClientRestriction::toDTO)
                .reduce(
                        (cr1, cr2) -> {
                            cr1.authorityLevelNameList().addAll(cr2.authorityLevelNameList());
                            return cr1;
                        })
                .orElseThrow(EntityNotFoundException::new);
    }

}
