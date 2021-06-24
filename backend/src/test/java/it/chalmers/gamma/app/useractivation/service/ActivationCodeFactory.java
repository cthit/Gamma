package it.chalmers.gamma.app.useractivation.service;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.UserActivationToken;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static UserActivationEntity create(Cid cid, UserActivationToken token) {
        return new UserActivationEntity(cid, token);
    }

}
