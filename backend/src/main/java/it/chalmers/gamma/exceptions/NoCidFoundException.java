package it.chalmers.gamma.exceptions;

public class NoCidFoundException extends CustomHttpStatus{
    public NoCidFoundException(){
        this(463, "NO_CID_FOUND", "no cid with that name found", "no cid with that name found");
    }
    public NoCidFoundException(int code, String status, String message, String error){
        super(code, status, message, error);
    }
}
