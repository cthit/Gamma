package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.client.data.ClientUserAccessDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApprovedClientsResponse {

    @JsonValue
    private final List<ClientUserAccessDTO> clients;

    public ApprovedClientsResponse(List<ClientUserAccessDTO> clients) {
        this.clients = clients;
    }

    public List<ClientUserAccessDTO> getClients() {
        return this.clients;
    }

    public ApprovedClientsResponseObject toResponseObject() {
        return new ApprovedClientsResponseObject(this);
    }

    public static class ApprovedClientsResponseObject extends ResponseEntity<ApprovedClientsResponse> {
        ApprovedClientsResponseObject(ApprovedClientsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
