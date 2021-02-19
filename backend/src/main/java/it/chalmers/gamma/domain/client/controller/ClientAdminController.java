package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.service.ClientService;
import it.chalmers.gamma.domain.client.controller.request.AddOrEditClientRequest;
import it.chalmers.gamma.domain.client.controller.response.ClientAddedResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientAddedResponse.ClientAddedResponseObject;
import it.chalmers.gamma.domain.client.controller.response.ClientEditedResponse;
import it.chalmers.gamma.domain.client.controller.response.GetAllClientsResponse;
import it.chalmers.gamma.domain.client.controller.response.GetAllClientsResponse.GetAllClientResponseObject;
import it.chalmers.gamma.domain.client.controller.response.GetClientResponse;
import it.chalmers.gamma.domain.client.controller.response.GetClientResponse.GetClientResponseObject;
import it.chalmers.gamma.domain.client.controller.response.ClientDoesNotExistResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientRemovedResponse;

import java.util.UUID;

import it.chalmers.gamma.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/clients")
public class ClientAdminController {

    private final ClientFinder clientFinder;
    private final ClientService clientService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAdminController.class);

    public ClientAdminController(ClientFinder clientFinder,
                                 ClientService clientService) {
        this.clientFinder = clientFinder;
        this.clientService = clientService;
    }

    @PostMapping()
    public ClientAddedResponseObject addITClient(@RequestBody AddOrEditClientRequest request) {
        String clientSecret = "{noop}" + TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );

        String clientId = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );

        this.clientService.createClient(
                new ClientDTO.ClientDTOBuilder()
                        .name(request.getName())
                        .webServerRedirectUri(request.getWebServerRedirectUri())
                        .autoApprove(request.isAutoApprove())
                        .description(request.getDescription())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build()
        );

        return new ClientAddedResponse(clientSecret).toResponseObject();
    }

    @GetMapping()
    public GetAllClientResponseObject getClients() {
        return new GetAllClientsResponse(this.clientFinder.getClients()).toResponseObject();
    }

    @GetMapping("/{clientId}")
    public GetClientResponseObject getClient(@PathVariable("clientId") String id) {
        try {
            return new GetClientResponse(this.clientFinder.getClient(id)).toResponseObject();
        } catch (ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientRemovedResponse removeClient(@PathVariable("clientId") String id) {
        try {
            this.clientService.removeClient(id);
            return new ClientRemovedResponse();
        } catch (ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

    @PutMapping("/{clientId}")
    public ClientEditedResponse editClient(@PathVariable("clientId") String clientId, @RequestBody AddOrEditClientRequest request) {
        try {
            this.clientService.editClient(requestToDTO(request, clientId));
            return new ClientEditedResponse();
        } catch (IDsNotMatchingException | ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

    private ClientDTO requestToDTO(AddOrEditClientRequest request) {
        return this.requestToDTO(request, null);
    }

    private ClientDTO requestToDTO(AddOrEditClientRequest request, String clientId) {
        return new ClientDTO.ClientDTOBuilder()
                .clientId(clientId)
                .webServerRedirectUri(request.getWebServerRedirectUri())
                .name(request.getName())
                .autoApprove(request.isAutoApprove())
                .description(request.getDescription())
                .build();
    }

}
