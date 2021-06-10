package it.chalmers.gamma.util.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountDeletedController {

    @GetMapping("/account-deleted")
    public String getAccountDeleted() {
        return "accountdeleted";
    }
}
