package it.chalmers.gamma.exceptions;

public class CodeMissmatchException extends CustomHttpStatus{
    public CodeMissmatchException(){
        this(462, "MISSMATCHED_CODE", "Code did not match cid", "Code did not match cid");
    }
    public CodeMissmatchException(int code, String status, String message, String error){
        super(code, status, message, error);
    }
}
