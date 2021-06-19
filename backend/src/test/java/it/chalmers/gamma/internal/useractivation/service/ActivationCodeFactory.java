package it.chalmers.gamma.internal.useractivation.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.UserActivationToken;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static UserActivationEntity create(Cid cid, UserActivationToken token) {
        return new UserActivationEntity(cid, token);
    }

}
