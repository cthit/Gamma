package it.chalmers.demo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping
public class MeController {

    @GetMapping("/me")
    public Object user(Authentication authentication) {
        System.out.println("WHAT");
        System.out.println(authentication);
        return authentication;
    }

    @GetMapping("/lol")
    public String lol() {
        System.out.println("what");
        return "lol";
    }

}
