package it.chalmers.gamma.adapter.primary.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountDeletedController {

  @GetMapping("/account-deleted")
  public String getAccountDeleted() {
    // TODO: Remove cookie
    return "accountdeleted";
  }
}
