package it.chalmers.gamma.response;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GoldappsReponse extends ResponseEntity<JSONObject> {
    public GoldappsReponse(JSONObject body) {
        super(body, HttpStatus.ACCEPTED);
    }
}
