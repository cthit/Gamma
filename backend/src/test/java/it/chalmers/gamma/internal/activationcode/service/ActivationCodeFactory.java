package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.util.domain.Cid;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static ActivationCode create(Cid cid, Code code) {
        return new ActivationCode(cid, code);
    }

}
