package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.service.ITUserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ITUserController {

    private final ITUserService itUserService;

    public ITUserController(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @GetMapping
    public List<ITUser> getAllITUsers() {
        return itUserService.findAll();
    }

}
