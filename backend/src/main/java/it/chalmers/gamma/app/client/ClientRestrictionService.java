package it.chalmers.gamma.app.client;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRestrictionEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRestrictionPK;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRestrictionRepository;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientRestriction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientRestrictionService {

    private final ClientRestrictionRepository clientRestrictionRepository;

    public ClientRestrictionService(ClientRestrictionRepository clientRestrictionRepository) {
        this.clientRestrictionRepository = clientRestrictionRepository;
    }

    public void create(ClientId clientId, List<AuthorityLevelName> restrictions) {
        this.clientRestrictionRepository.saveAll(
                restrictions
                        .stream()
                        .map(authorityLevelName -> new ClientRestrictionEntity(
                                new ClientRestrictionPK(
                                        clientId,
                                        authorityLevelName
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }

    public void delete(ClientRestrictionPK id) {
       this.clientRestrictionRepository.deleteById(id);
    }

    public List<AuthorityLevelName> get(ClientId clientId) {
        return this.clientRestrictionRepository.findClientRestrictionsById_ClientId(clientId)
                .stream()
                .map(ClientRestrictionEntity::toDomain)
                .map(ClientRestriction::authorityLevelName)
                .collect(Collectors.toList());
    }

    public static class ClientRestrictionNotFoundException extends Exception { }

}
