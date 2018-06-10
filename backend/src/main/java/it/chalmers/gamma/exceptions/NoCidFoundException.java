package it.chalmers.gamma.exceptions;

public class NoCidFoundException extends Exception{
    public NoCidFoundException(){
        this("no cid with that name found");
    }
    public NoCidFoundException(String message){
        super(message);
    }
}
