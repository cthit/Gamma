package it.chalmers.gamma.exceptions;

import org.springframework.http.HttpStatus;

public class CIDAlreadyWhitelistedException extends CustomHttpStatus{
    public CIDAlreadyWhitelistedException(){
        this(460, "CID_ALREADY_FOUND", "cid already exists in the whitelist table", "cid already exists in the whitelist table");
    }
    public CIDAlreadyWhitelistedException(int code, String status, String message, String error){
        super(code, status, message, error);
    }
}
