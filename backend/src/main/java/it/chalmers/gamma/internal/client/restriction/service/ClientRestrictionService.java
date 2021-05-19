package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityHasUsagesException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ClientRestrictionService implements CreateEntity<ClientRestrictionDTO>, DeleteEntity<ClientRestrictionPK> {

    private final ClientRestrictionRepository clientRestrictionRepository;

    public ClientRestrictionService(ClientRestrictionRepository clientRestrictionRepository) {
        this.clientRestrictionRepository = clientRestrictionRepository;
    }

    @Override
    public void create(ClientRestrictionDTO clientRestriction) throws EntityAlreadyExistsException {
        this.clientRestrictionRepository.saveAll(
                clientRestriction.authorityLevelNameList()
                        .stream()
                        .map(authorityLevelName -> new ClientRestriction(
                                new ClientRestrictionPK(
                                        clientRestriction.clientId(),
                                        authorityLevelName
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void delete(ClientRestrictionPK id) throws EntityNotFoundException {
       this.clientRestrictionRepository.deleteById(id);
    }
}
