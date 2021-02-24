package it.chalmers.gamma.domain.client.controller.request;

import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;


public class CreateClientRequest {

    public String webServerRedirectUri;
    public String name;
    public boolean autoApprove;
    public TextDTO description;

}
