package it.chalmers.gamma.exceptions;

public class CodeExpiredException extends Exception{
    public CodeExpiredException() {this("The received activation code has expired. ");}

    public CodeExpiredException(String message) {
        super(message);
    }
}
