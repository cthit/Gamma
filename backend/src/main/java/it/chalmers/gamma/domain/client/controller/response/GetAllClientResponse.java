package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;

import java.util.List;

public class GetAllClientResponse {

    @JsonValue
    public final List<ClientDTO> clients;

    public GetAllClientResponse(List<ClientDTO> clients) {
        this.clients = clients;
    }

}
