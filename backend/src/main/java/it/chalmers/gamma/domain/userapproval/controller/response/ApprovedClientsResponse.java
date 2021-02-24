package it.chalmers.gamma.domain.userapproval.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.client.data.dto.ClientUserAccessDTO;

import java.util.List;

public class ApprovedClientsResponse {

    @JsonValue
    private final List<ClientUserAccessDTO> clients;

    public ApprovedClientsResponse(List<ClientUserAccessDTO> clients) {
        this.clients = clients;
    }

}
