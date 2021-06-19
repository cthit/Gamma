package it.chalmers.gamma.util.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController  {

    /*
        Right now, a lot of debug information is lost due to this solution.
        Sometimes, a random 403 is thrown in certain situations (See issue #798). And this solution to printing the
        error means that important information is neither logged or returned in the response.

        @GetMapping("/error")
        public String displayError(HttpServletRequest request, Model model) {
            Object statusCodeString = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            int statusCode = Integer.parseInt(statusCodeString.toString());
            if (HttpStatus.NOT_FOUND.value() == statusCode) {
                model.addAttribute("original_page",
                        request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
                return "error-404";
            }
            if (HttpStatus.valueOf(statusCode).is5xxServerError()) {
                return "error-5xx";
            }
            if (HttpStatus.UNPROCESSABLE_ENTITY.value() == statusCode) {
                model.addAttribute("error_message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
                return "error-422";
            }
            model.addAttribute("error_code", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
            model.addAttribute("error_message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
            model.addAttribute("origin_url", request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
            return "common-error";
        }
    */

}
