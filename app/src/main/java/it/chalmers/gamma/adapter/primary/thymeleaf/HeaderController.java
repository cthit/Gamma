package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class HeaderController {

    @GetMapping("/header")
    public String getHeader(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
        return "common/header";
    }

}
