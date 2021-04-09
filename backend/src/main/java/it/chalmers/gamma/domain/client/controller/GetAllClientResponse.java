package it.chalmers.gamma.domain.client.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.service.ClientDTO;

import java.util.List;

public class GetAllClientResponse {

    @JsonValue
    protected final List<ClientDTO> clients;

    protected GetAllClientResponse(List<ClientDTO> clients) {
        this.clients = clients;
    }

}
