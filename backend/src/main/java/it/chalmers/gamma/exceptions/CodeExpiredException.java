package it.chalmers.gamma.exceptions;

public class CodeExpiredException extends CustomHttpStatus{
    public CodeExpiredException() {this(461, "CODE_EXPIRED" ,"The received activation code has expired.", "The received activation code has expired.");}

    public CodeExpiredException(int code, String status, String message, String error) {
        super(code, status, message, error);
    }
}
