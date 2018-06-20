package it.chalmers.gamma.exceptions;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(){
        this("a user with that cid already exists");
    }
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
