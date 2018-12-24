package it.chalmers.gamma.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring5.view.ThymeleafView;

@Controller
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting() {
        System.out.println("getting greeting");
        return "greeting";
    }
}
