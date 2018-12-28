package it.chalmers.gamma.controller;

import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.ValidateJwtRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.ValidJwtResponse;

import it.chalmers.gamma.util.InputValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/validate_jwt")
public final class JwtController {

    private final JwtTokenProvider tokenProvider;

    public JwtController(JwtTokenProvider jwtTokenProvider) {
        this.tokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<Boolean> tokenIsValid(@Valid @RequestBody ValidateJwtRequest validateJwtRequest,
                                                BindingResult result) {
        if(result.hasErrors()){
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        return new ValidJwtResponse(this.tokenProvider.validateToken(validateJwtRequest.getJwt()));
    }
}
