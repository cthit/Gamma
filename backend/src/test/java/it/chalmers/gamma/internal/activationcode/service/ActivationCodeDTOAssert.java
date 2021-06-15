package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.ActivationCode;
import it.chalmers.gamma.domain.Cid;
import org.assertj.core.api.AbstractAssert;

import java.time.Instant;

public class ActivationCodeDTOAssert extends AbstractAssert<ActivationCodeDTOAssert, ActivationCode> {

    public ActivationCodeDTOAssert(ActivationCode activationCode) {
        super(activationCode, ActivationCodeDTOAssert.class);
    }

    public static ActivationCodeDTOAssert assertThat(ActivationCode actual) {
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
        if(!actual.token().get().matches("^([0-9]{8})$")) {
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
