package it.chalmers.gamma.exceptions;

public class CodeMissmatchException extends Exception{
    public CodeMissmatchException(){
        this("Code did not match cid");
    }
    public CodeMissmatchException(String error){
        super(error);
    }
}
