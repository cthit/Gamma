package it.chalmers.gamma.exceptions;

public class PasswordTooShortException extends CustomHttpStatus{
    public PasswordTooShortException(){
        this(464, "TOO_SHORT_PASSWORD", "the supplied password is too short", "the supplied password is too short");
    }
    public PasswordTooShortException(int code, String status, String message, String error){
        super(code, status, message, error);
    }
}
