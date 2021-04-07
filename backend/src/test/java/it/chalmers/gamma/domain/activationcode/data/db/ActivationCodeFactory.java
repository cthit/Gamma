package it.chalmers.gamma.domain.activationcode.data.db;

import it.chalmers.gamma.domain.activationcode.Code;
import it.chalmers.gamma.util.domain.Cid;

public class ActivationCodeFactory {

    private ActivationCodeFactory() { }

    public static ActivationCode create(Cid cid, Code code) {
        return new ActivationCode(cid, code);
    }

}
