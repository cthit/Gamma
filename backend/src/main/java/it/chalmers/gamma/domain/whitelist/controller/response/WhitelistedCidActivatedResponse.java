package it.chalmers.gamma.domain.whitelist.controller.response;

import it.chalmers.gamma.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// This will be thrown even if there was an error for security reasons.
public class WhitelistedCidActivatedResponse extends SuccessResponse { }
