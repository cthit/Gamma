package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.primary.api.ApiExceptionAdvice;
import it.chalmers.gamma.app.oauth2.GammaAuthorizationService;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Renders errors for all request methods. Like Spring Boot's BasicErrorController, clients that
 * accept HTML get an error page while other clients (e.g. API clients) get a JSON body.
 */
@Controller
@RequestMapping("/error")
public class GammaErrorController implements ErrorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GammaErrorController.class);

  @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView handleErrorHtml(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response,
      HttpServletRequest request) {
    HttpStatusCode status = getStatus(request);
    Exception exception = getException(request);
    if (exception != null) {
      LOGGER.error("Caught error, rendering error page...", exception);
    }

    String page = "pages/error";

    if (status.value() == HttpStatus.NOT_FOUND.value()) {
      page = "pages/404";
    } else if (exception instanceof GammaAuthorizationService.UserNotAllowedRuntimeException) {
      page = "pages/no-access-to-client";
    }

    ModelAndView mv = new ModelAndView();
    mv.setStatus(status);
    if (htmxRequest) {
      response.addHeader("HX-Retarget", "body");
      response.addHeader("HX-Reswap", "innerHTML");

      mv.setViewName(page);
    } else {
      mv.setViewName("index");
      mv.addObject("page", page);
    }

    mv.addObject("statusCode", status.value());
    mv.addObject("statusReason", getReasonPhrase(status));

    if (isAdmin()) {
      if (exception != null) {
        mv.addObject("exceptionMessage", exception.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        mv.addObject("exceptionStackTrace", sw.toString());
      } else {
        String errorMessage = getErrorMessage(request);
        if (errorMessage != null) {
          mv.addObject("exceptionMessage", errorMessage);
        }
      }
    }

    return mv;
  }

  @RequestMapping
  public ResponseEntity<ApiExceptionAdvice.ApiErrorBody> handleError(HttpServletRequest request) {
    HttpStatusCode status = getStatus(request);
    Exception exception = getException(request);
    if (exception != null) {
      LOGGER.error("Caught error, returning error response...", exception);
    }

    String reason = getReasonPhrase(status);
    String message = null;
    if (isAdmin()) {
      message = exception != null ? exception.getMessage() : getErrorMessage(request);
    }
    if (message == null || message.isBlank()) {
      message = reason;
    }

    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ApiExceptionAdvice.ApiErrorBody(status.value(), reason, message));
  }

  private static HttpStatusCode getStatus(HttpServletRequest request) {
    Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    try {
      return HttpStatusCode.valueOf(Integer.parseInt(statusCode.toString()));
    } catch (IllegalArgumentException e) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  private static String getReasonPhrase(HttpStatusCode status) {
    HttpStatus resolved = HttpStatus.resolve(status.value());
    return resolved != null ? resolved.getReasonPhrase() : "Error";
  }

  private static Exception getException(HttpServletRequest request) {
    return request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Exception exception
        ? exception
        : null;
  }

  private static String getErrorMessage(HttpServletRequest request) {
    Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
    return errorMessage != null ? errorMessage.toString() : null;
  }

  private static boolean isAdmin() {
    return AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userAuth
        && userAuth.isAdmin();
  }
}
