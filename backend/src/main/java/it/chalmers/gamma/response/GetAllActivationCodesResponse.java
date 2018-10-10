package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ActivationCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetAllActivationCodesResponse extends ResponseEntity<List<ActivationCode>> {

    public GetAllActivationCodesResponse(List<ActivationCode> activationCodeList){
        super(activationCodeList, HttpStatus.OK);
    }

}
