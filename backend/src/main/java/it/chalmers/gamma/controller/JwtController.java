package it.chalmers.gamma.controller;

import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.ValidateJwtRequest;
import it.chalmers.gamma.response.ValidJwtResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate_jwt")
public class JwtController {

    private final JwtTokenProvider tokenProvider;

    public JwtController(JwtTokenProvider jwtTokenProvider) {
        this.tokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<Boolean> isValid(@RequestBody ValidateJwtRequest validateJwtRequest) {
        return new ValidJwtResponse(this.tokenProvider.validateToken(validateJwtRequest.getJwt()));
    }
}
