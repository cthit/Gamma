package it.chalmers.gamma.exceptions;

public class UserAlreadyExistsException extends CustomHttpStatus{
    public UserAlreadyExistsException(){
        this(465, "USER_ALREADY_REGISTERED", "a user with that cid already exists", "a user with that cid already exists");
    }
    public UserAlreadyExistsException(int code, String status, String message, String error){
        super(code, status, message, error);
    }
}
