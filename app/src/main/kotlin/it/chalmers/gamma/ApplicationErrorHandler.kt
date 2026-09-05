package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiAccessConflict
import it.chalmers.gamma.apiaccess.views.ApiError
import it.chalmers.gamma.media.MediaTooLarge
import it.chalmers.gamma.oauth.OAuthClientConflict
import it.chalmers.gamma.organization.OrganizationConflict
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.redis.RedisUnavailable
import it.chalmers.gamma.users.UserConflict
import it.chalmers.gamma.users.views.renderErrorPage
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.webmvc.error.ErrorAttributes
import org.springframework.boot.webmvc.error.ErrorController
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class ApplicationErrorHandler {
    @ExceptionHandler(AccessDenied::class)
    fun accessDenied(response: HttpServletResponse): String =
        errorPage(response, HttpStatus.FORBIDDEN, "Unauthorized", "You are not authorized to view this page.")

    @ExceptionHandler(
        MediaTooLarge::class,
        MaxUploadSizeExceededException::class,
    )
    fun uploadTooLarge(response: HttpServletResponse): String {
        response.setHeader("HX-Retarget", "body")
        return errorPage(response, HttpStatus.CONTENT_TOO_LARGE, "Upload too large", "The uploaded file is too large.")
    }

    @ExceptionHandler(
        ApiAccessConflict::class,
        UserConflict::class,
        OAuthClientConflict::class,
        OrganizationConflict::class,
    )
    fun conflict(response: HttpServletResponse): String =
        errorPage(response, HttpStatus.CONFLICT, "Conflict", "The request conflicts with the current state.")

    @ExceptionHandler(RedisUnavailable::class)
    fun serviceUnavailable(response: HttpServletResponse): String =
        errorPage(response, HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable", "Please try again later.")

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(response: HttpServletResponse): String =
        errorPage(response, HttpStatus.BAD_REQUEST, "Bad request", "The request was invalid.")

    private fun errorPage(
        response: HttpServletResponse,
        status: HttpStatus,
        title: String,
        message: String,
    ): String {
        response.status = status.value()
        response.contentType = "text/html;charset=UTF-8"
        return renderErrorPage(status.value(), title, message)
    }
}

@Controller
class ApplicationErrorController(
    private val errorAttributes: ErrorAttributes,
) : ErrorController {
    private val log = LoggerFactory.getLogger(javaClass)

    @ResponseBody
    @GetMapping("/error", produces = ["text/html"])
    fun error(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): String {
        val statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as? Int ?: 500
        val status = HttpStatus.resolve(statusCode) ?: HttpStatus.INTERNAL_SERVER_ERROR
        val failure = errorAttributes.getError(ServletWebRequest(request))
        if (failure == null) {
            log.error("Unhandled request failed with status {}", statusCode)
        } else {
            log.error("Unhandled request failed", failure)
        }

        response.status = statusCode
        response.contentType = "text/html;charset=UTF-8"
        return renderErrorPage(statusCode, status.reasonPhrase, "The request could not be completed.")
    }
}

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = [RestApiController::class])
class RestApiErrorHandler {
    @ExceptionHandler(AccessDenied::class)
    fun accessDenied() = apiError(HttpStatus.FORBIDDEN, "FORBIDDEN")

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest() = apiError(HttpStatus.BAD_REQUEST, "BAD_REQUEST")

    @ExceptionHandler(RedisUnavailable::class)
    fun unavailable() = apiError(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE")

    private fun apiError(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(status).body(ApiError(status.value(), status.reasonPhrase, message))
}
