package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientRestrictions;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ClientRestrictionService {

    private final ClientRestrictionRepository clientRestrictionRepository;

    public ClientRestrictionService(ClientRestrictionRepository clientRestrictionRepository) {
        this.clientRestrictionRepository = clientRestrictionRepository;
    }

    public void create(ClientRestrictions clientRestriction) {
        this.clientRestrictionRepository.saveAll(
                clientRestriction.authorityLevelNameList()
                        .stream()
                        .map(authorityLevelName -> new ClientRestrictionEntity(
                                new ClientRestrictionPK(
                                        clientRestriction.clientId(),
                                        authorityLevelName
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }

    public void delete(ClientRestrictionPK id) {
       this.clientRestrictionRepository.deleteById(id);
    }

    public ClientRestrictions get(ClientId clientId) throws ClientRestrictionNotFoundException {
        return this.clientRestrictionRepository.findClientRestrictionsById_ClientId(clientId)
                .stream()
                .map(ClientRestrictionEntity::toDTO)
                .reduce(
                        (cr1, cr2) -> {
                            cr1.authorityLevelNameList().addAll(cr2.authorityLevelNameList());
                            return cr1;
                        })
                .orElseThrow(ClientRestrictionNotFoundException::new);
    }

    public static class ClientRestrictionNotFoundException extends Exception { }

}
