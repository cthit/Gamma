package it.chalmers.gamma.exceptions;

public class PasswordTooShortException extends Exception{
    public PasswordTooShortException(){
        this("that password is too short");
    }
    public PasswordTooShortException(String message){
        super(message);
    }
}
