package it.chalmers.gamma.domain.activationcode.service;

import it.chalmers.gamma.domain.activationcode.service.ActivationCodeDTO;
import it.chalmers.gamma.util.domain.Cid;
import org.assertj.core.api.AbstractAssert;

import java.time.Instant;

public class ActivationCodeDTOAssert extends AbstractAssert<ActivationCodeDTOAssert, ActivationCodeDTO> {

    public ActivationCodeDTOAssert(ActivationCodeDTO activationCode) {
        super(activationCode, ActivationCodeDTOAssert.class);
    }

    public static ActivationCodeDTOAssert assertThat(ActivationCodeDTO actual) {
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
        if(!actual.code().get().matches("^([0-9]{8})$")) {
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
