package it.chalmers.gamma.response.client;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.dto.access.ITClientUserAccessDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApprovedITClientsResponse {

    @JsonValue
    private final List<ITClientUserAccessDTO> clients;

    public ApprovedITClientsResponse(List<ITClientUserAccessDTO> clients) {
        this.clients = clients;
    }

    public List<ITClientUserAccessDTO> getClients() {
        return this.clients;
    }

    public ApprovedITClientsResponseObject toResponseObject() {
        return new ApprovedITClientsResponseObject(this);
    }

    public static class ApprovedITClientsResponseObject extends ResponseEntity<ApprovedITClientsResponse> {
        ApprovedITClientsResponseObject(ApprovedITClientsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
