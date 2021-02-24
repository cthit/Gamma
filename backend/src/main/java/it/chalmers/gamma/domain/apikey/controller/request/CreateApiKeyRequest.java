package it.chalmers.gamma.domain.apikey.controller.request;

import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class CreateApiKeyRequest {

    public String name;
    public TextDTO description;

}
