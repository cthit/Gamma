package it.chalmers.gamma.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String displayError(HttpServletRequest request) {
        Object statusCodeString = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = Integer.parseInt(statusCodeString.toString());
        if (HttpStatus.NOT_FOUND.value() == statusCode) {
            return "error-404";
        }
        if(HttpStatus.valueOf(statusCode).is5xxServerError()) {
            return "error-5xx";
        }
        return "standard-error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
