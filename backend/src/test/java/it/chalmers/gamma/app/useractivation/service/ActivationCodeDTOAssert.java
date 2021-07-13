package it.chalmers.gamma.app.useractivation.service;

import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.domain.Cid;
import org.assertj.core.api.AbstractAssert;

import java.time.Instant;

public class ActivationCodeDTOAssert extends AbstractAssert<ActivationCodeDTOAssert, UserActivation> {

    public ActivationCodeDTOAssert(UserActivation userActivation) {
        super(userActivation, ActivationCodeDTOAssert.class);
    }

    public static ActivationCodeDTOAssert assertThat(UserActivation actual) {
        return new ActivationCodeDTOAssert(actual);
    }

    public ActivationCodeDTOAssert hasCorrectCid(Cid cid) {
        isNotNull();
        if(!actual.cid().equals(cid)) {
            failWithMessage("ActivationCode doesn't have the same cid as created with");
        }
        return this;
    }

    public ActivationCodeDTOAssert hasValidCode() {
        isNotNull();
        if(!actual.token().value().matches("^([0-9]{8})$")) {
            failWithMessage("ActivationCode doesn't match the regex [0-9]{8}");
        }
        return this;
    }

    public ActivationCodeDTOAssert hasCreatedAtDateNotInFuture() {
        isNotNull();
        if(Instant.now().compareTo(actual.createdAt()) < 0) {
            failWithMessage("ActivationCode has a createdAt that's in the future");
        }
        return this;
    }

}
