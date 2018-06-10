package it.chalmers.gamma.exceptions;

public class CIDAlreadyWhitelistedException extends Exception{
    public CIDAlreadyWhitelistedException(){
        this("cid already exists in the whitelist table");
    }
    public CIDAlreadyWhitelistedException(String message){
        super(message);
    }
}
