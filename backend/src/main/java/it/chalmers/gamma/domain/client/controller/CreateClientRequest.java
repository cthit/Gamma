package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;


public class CreateClientRequest {

    protected CreateClientRequest() {
        
    }
    
    protected String webServerRedirectUri;
    protected String name;
    protected boolean autoApprove;
    protected TextDTO description;

}
