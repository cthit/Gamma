package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static ActivationCodeEntity create(Cid cid, Code code) {
        return new ActivationCodeEntity(cid, code);
    }

}
