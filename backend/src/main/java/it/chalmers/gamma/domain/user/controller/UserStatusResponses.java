package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;

public class UserStatusResponses {

    private UserStatusResponses() { }

    public static class PasswordChangedResponse extends SuccessResponse { }
    public static class UserCreatedResponse extends SuccessResponse { }
    public static class UserDeletedResponse extends SuccessResponse { }
    public static class UserEditedResponse extends SuccessResponse { }
    public static class PasswordRestLinkSentResponse extends SuccessResponse { }

    public static class UserNotFoundResponse extends ErrorResponse {
        public UserNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }
    public static class UserAlreadyExistsResponse extends ErrorResponse {
        public UserAlreadyExistsResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    public static class PasswordTooShortResponse extends ErrorResponse {
        public PasswordTooShortResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    public static class IncorrectCidOrPasswordResponse extends ErrorResponse {
        public IncorrectCidOrPasswordResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
