package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/lol")
    public String getIndex() {
        return "lol";
    }

    @GetMapping("/test")
    public String getTest() {
        return "test";
    }

}
