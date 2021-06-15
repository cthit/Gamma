package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.ActivationCodeToken;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static ActivationCodeEntity create(Cid cid, ActivationCodeToken token) {
        return new ActivationCodeEntity(cid, token);
    }

}
