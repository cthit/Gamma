package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ActivationCodeServiceTest {

    @Mock
    private ActivationCodeRepository activationCodeRepository;

    @InjectMocks
    private ActivationCodeService activationCodeService;

    @Test
    void savedActivationCode() {
        given(activationCodeRepository.save(any(ActivationCode.class)))
                .willAnswer(returnsFirstArg());

        Cid cid = new Cid("mycid");
        ActivationCodeDTO activationCode = activationCodeService.saveActivationCode(cid);

        ActivationCodeDTOAssert.assertThat(activationCode)
                .hasCorrectCid(cid)
                .hasValidCode()
                .hasCreatedAtDateNotInFuture();
    }

    @Test
    void delete() {
        Cid cid = new Cid("mycid");
        Cid cid2 = new Cid("mycidi");

        willThrow(IllegalArgumentException.class)
                .given(activationCodeRepository)
                .deleteById(any(Cid.class));
        willDoNothing()
                .given(activationCodeRepository)
                .deleteById(cid);

        assertThatNoException()
                .isThrownBy(() -> activationCodeService.delete(cid));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> activationCodeService.delete(cid2));
    }

}